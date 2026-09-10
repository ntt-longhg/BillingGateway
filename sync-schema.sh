#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCHEMA_FILE="$ROOT_DIR/src/main/resources/schema.sql"
DRY_RUN=false

if [[ "${1:-}" == "--dry-run" ]]; then
    DRY_RUN=true
fi

echo "=========================================="
echo "       JPA -> schema.sql COMMENT SYNC"
echo "=========================================="
echo "ROOT  : $ROOT_DIR"
echo "SCHEMA: $SCHEMA_FILE"
echo "MODE  : $([[ "$DRY_RUN" == true ]] && echo "DRY-RUN" || echo "WRITE")"
echo

if [[ ! -f "$SCHEMA_FILE" ]]; then
    echo "ERROR: schema.sql not found:"
    echo "  $SCHEMA_FILE"
    exit 1
fi

python3 - "$ROOT_DIR" "$SCHEMA_FILE" "$DRY_RUN" <<'PY'
import sys
import re
from pathlib import Path
from datetime import datetime

root = Path(sys.argv[1])
schema_file = Path(sys.argv[2])
dry_run = sys.argv[3].lower() == "true"


# ------------------------------------------------------------
# Helpers
# ------------------------------------------------------------

def sql_escape(value):
    """
    SQL string escaping:
        ' -> ''
    """
    return value.replace("'", "''")


def java_string(value):
    """
    Basic Java string unescape.
    Enough for JPA comment strings.
    """
    value = value.strip()

    if len(value) >= 2 and value[0] == '"' and value[-1] == '"':
        value = value[1:-1]

    value = value.replace(r'\"', '"')
    value = value.replace(r"\'", "'")
    value = value.replace(r'\\', '\\')

    return value


def extract_string_attribute(annotation, name):
    """
    Extract:

        comment = "some text"

    without using a regex that breaks when the text contains ')'.
    """

    pattern = re.compile(
        rf'\b{re.escape(name)}\s*=\s*"((?:\\.|[^"\\])*)"'
    )

    match = pattern.search(annotation)

    if not match:
        return None

    return java_string(match.group(1))


def extract_annotation_blocks(text, annotation_name):
    """
    Extract complete:

        @Column(...)
        @JoinColumn(...)
        @Table(...)

    blocks while correctly handling nested parentheses
    and strings containing ')' characters.
    """

    result = []

    pattern = re.compile(
        rf'@{re.escape(annotation_name)}\s*\('
    )

    for match in pattern.finditer(text):
        start = match.start()
        pos = match.end()

        depth = 1
        in_string = False
        escaped = False

        while pos < len(text):
            ch = text[pos]

            if in_string:
                if escaped:
                    escaped = False
                elif ch == '\\':
                    escaped = True
                elif ch == '"':
                    in_string = False
            else:
                if ch == '"':
                    in_string = True
                elif ch == '(':
                    depth += 1
                elif ch == ')':
                    depth -= 1

                    if depth == 0:
                        result.append(text[start:pos + 1])
                        break

            pos += 1

    return result


def extract_name(annotation):
    match = re.search(
        r'\bname\s*=\s*"((?:\\.|[^"\\])*)"',
        annotation
    )

    if not match:
        return None

    return java_string(match.group(1))


def find_java_entities():
    """
    Find Java files containing @Entity.
    """

    java_files = []

    for path in root.rglob("*.java"):
        try:
            text = path.read_text(encoding="utf-8")
        except Exception:
            continue

        if "@Entity" in text:
            java_files.append(path)

    return java_files


# ------------------------------------------------------------
# Parse JPA
# ------------------------------------------------------------

tables = {}

java_files = find_java_entities()

print(f"Java entities : {len(java_files)}")
print()

for java_file in java_files:

    text = java_file.read_text(encoding="utf-8")

    table_blocks = extract_annotation_blocks(text, "Table")

    if not table_blocks:
        continue

    table_annotation = table_blocks[0]

    table_name = extract_name(table_annotation)
    table_comment = extract_string_attribute(
        table_annotation,
        "comment"
    )

    if not table_name:
        continue

    entity = {
        "file": str(java_file.relative_to(root)),
        "table": table_name,
        "comment": table_comment,
        "columns": {},
    }

    # @Column
    for annotation in extract_annotation_blocks(text, "Column"):

        column_name = extract_name(annotation)

        if not column_name:
            continue

        comment = extract_string_attribute(
            annotation,
            "comment"
        )

        if comment is not None:
            entity["columns"][column_name] = comment

    # @JoinColumn
    for annotation in extract_annotation_blocks(text, "JoinColumn"):

        column_name = extract_name(annotation)

        if not column_name:
            continue

        comment = extract_string_attribute(
            annotation,
            "comment"
        )

        if comment is not None:
            entity["columns"][column_name] = comment

    tables[table_name] = entity


# ------------------------------------------------------------
# Print discovered JPA comments
# ------------------------------------------------------------

print("Discovered JPA comments")
print("------------------------------------------")

for table_name, entity in sorted(tables.items()):

    print(f"TABLE {table_name}")

    if entity["comment"]:
        print(f"  COMMENT: {entity['comment']}")

    for column_name, comment in entity["columns"].items():
        print(f"  COLUMN {column_name}: {comment}")

    print()


# ------------------------------------------------------------
# Read schema
# ------------------------------------------------------------

schema = schema_file.read_text(encoding="utf-8")

original_schema = schema


# ------------------------------------------------------------
# SQL parser helpers
# ------------------------------------------------------------

def find_create_table_blocks(sql):
    """
    Find:

        CREATE TABLE ...

    blocks ending at the matching semicolon.

    This is intentionally tolerant of formatting.
    """

    pattern = re.compile(
        r'\bCREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?'
        r'[`"]?([A-Za-z0-9_]+)[`"]?\s*\(',
        re.IGNORECASE
    )

    blocks = []

    for match in pattern.finditer(sql):

        table_name = match.group(1)

        start = match.start()

        pos = match.end()

        depth = 1
        in_string = False
        quote = None
        escaped = False

        while pos < len(sql):

            ch = sql[pos]

            if in_string:

                if escaped:
                    escaped = False

                elif ch == '\\':
                    escaped = True

                elif ch == quote:
                    in_string = False

            else:

                if ch in ("'", '"', '`'):
                    in_string = True
                    quote = ch

                elif ch == '(':
                    depth += 1

                elif ch == ')':

                    depth -= 1

                    if depth == 0:

                        # Find semicolon after CREATE TABLE
                        end = sql.find(';', pos)

                        if end == -1:
                            end = pos + 1
                        else:
                            end += 1

                        blocks.append(
                            {
                                "name": table_name,
                                "start": start,
                                "end": end,
                                "body_start": match.end(),
                                "body_end": pos,
                            }
                        )

                        break

            pos += 1

    return blocks


def update_or_insert_table_comment(block_text, comment):

    escaped = sql_escape(comment)

    # Existing:
    # ) COMMENT='...';
    #
    # or:
    # ) COMMENT = '...';

    pattern = re.compile(
        r'(\)\s*COMMENT\s*=\s*\')'
        r'(.*?)'
        r'(\')',
        re.IGNORECASE | re.DOTALL
    )

    if pattern.search(block_text):

        return pattern.sub(
            lambda m: m.group(1) + escaped + m.group(3),
            block_text,
            count=1
        ), "updated"

    # No table comment.
    #
    # Insert immediately after closing ')' before ';'
    pattern = re.compile(
        r'\)\s*;',
        re.DOTALL
    )

    if pattern.search(block_text):

        return pattern.sub(
            f") COMMENT='{escaped}';",
            block_text,
            count=1
        ), "inserted"

    return block_text, "missing"


def split_sql_lines(body):
    """
    Return lines while preserving original line endings.
    """
    return body.splitlines(keepends=True)


def update_column_comment(body, column_name, comment):

    escaped = sql_escape(comment)

    lines = split_sql_lines(body)

    # Match column definition line:
    #
    # `column_name` varchar(...)
    #
    # or:
    #
    # column_name varchar(...)

    column_pattern = re.compile(
        rf'^(\s*)(?:`{re.escape(column_name)}`|'
        rf'"{re.escape(column_name)}"|'
        rf'{re.escape(column_name)})'
        rf'(\s+)',
        re.IGNORECASE
    )

    for i, line in enumerate(lines):

        if not column_pattern.search(line):
            continue

        # Existing COMMENT
        comment_pattern = re.compile(
            r'\s+COMMENT\s+\'(?:\'\'|[^\'])*\'',
            re.IGNORECASE
        )

        if comment_pattern.search(line):

            newline = comment_pattern.sub(
                f" COMMENT '{escaped}'",
                line,
                count=1
            )

            lines[i] = newline

            return ''.join(lines), "updated"

        # Insert COMMENT before comma.
        #
        # column ... NOT NULL,
        #
        # becomes:
        #
        # column ... NOT NULL COMMENT '...', 
        #

        newline = line.rstrip('\r\n')

        line_ending = line[len(newline):]

        comma_match = re.search(
            r',\s*$',
            newline
        )

        if comma_match:

            newline = (
                newline[:comma_match.start()]
                + f" COMMENT '{escaped}',"
            )

        else:

            newline += f" COMMENT '{escaped}'"

        lines[i] = newline + line_ending

        return ''.join(lines), "inserted"

    return body, "not_found"


# ------------------------------------------------------------
# Update schema
# ------------------------------------------------------------

blocks = find_create_table_blocks(schema)

print("Schema tables")
print("------------------------------------------")

for block in blocks:
    print(f"  {block['name']}")

print()


# Work backwards so offsets remain valid.
changes = []

for table_name, entity in tables.items():

    matching = [
        block
        for block in blocks
        if block["name"].lower() == table_name.lower()
    ]

    if not matching:

        print(f"[WARN] Table not found in schema: {table_name}")
        continue

    block = matching[0]

    old_block = schema[block["start"]:block["end"]]

    new_block = old_block

    # Table comment
    if entity["comment"]:

        new_block, status = update_or_insert_table_comment(
            new_block,
            entity["comment"]
        )

        if status != "missing":
            print(
                f"[TABLE] {table_name}: {status}"
            )

    # Column comments
    #
    # Recalculate body from modified block.
    table_blocks_new = find_create_table_blocks(new_block)

    if table_blocks_new:

        b = table_blocks_new[0]

        body_start = b["body_start"]
        body_end = b["body_end"]

        body = new_block[body_start:body_end]

        for column_name, comment in entity["columns"].items():

            body, status = update_column_comment(
                body,
                column_name,
                comment
            )

            if status == "not_found":

                print(
                    f"  [WARN] Column not found: "
                    f"{table_name}.{column_name}"
                )

            else:

                print(
                    f"  [COLUMN] "
                    f"{table_name}.{column_name}: {status}"
                )

        new_block = (
            new_block[:body_start]
            + body
            + new_block[body_end:]
        )

    if new_block != old_block:

        changes.append(
            (
                block["start"],
                block["end"],
                new_block
            )
        )


# ------------------------------------------------------------
# Apply changes
# ------------------------------------------------------------

for start, end, new_block in reversed(changes):

    schema = (
        schema[:start]
        + new_block
        + schema[end:]
    )


# ------------------------------------------------------------
# Result
# ------------------------------------------------------------

print()
print("==========================================")

if schema == original_schema:

    print("No changes required.")

else:

    print(
        f"Changes detected: {len(changes)} table(s)"
    )

    if dry_run:

        print("DRY-RUN: schema.sql was NOT modified.")

    else:

        timestamp = datetime.now().strftime(
            "%Y%m%d_%H%M%S"
        )

        backup = schema_file.with_suffix(
            schema_file.suffix + f".{timestamp}.bak"
        )

        backup.write_text(
            original_schema,
            encoding="utf-8"
        )

        schema_file.write_text(
            schema,
            encoding="utf-8"
        )

        print(f"Backup : {backup}")
        print(f"Written: {schema_file}")

print("==========================================")
PY
