import os
import re

# Cấu hình đường dẫn dự án
ENTITY_DIR = "src/main/java/com/gateway/billing/modules"
SCHEMA_FILE = "src/main/resources/schema.sql"

print("=== Syncing comments from entities to schema.sql ===")

if not os.path.exists(SCHEMA_FILE):
    print(f"⚠️ ERROR: Không tìm thấy file schema.sql tại đường dẫn: {SCHEMA_FILE}")
    exit(1)

# Đọc nội dung file schema.sql ban đầu
with open(SCHEMA_FILE, "r", encoding="utf-8") as f:
    schema_content = f.read()

print("Checking entity files...")

# Duyệt qua các file Java
for root, _, files in os.walk(ENTITY_DIR):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)

            with open(file_path, "r", encoding="utf-8") as f:
                java_content = f.read()

            # Chỉ xử lý các file có chứa @Entity
            if "@Entity" in java_content:
                print("----------------------------------------")
                print(f"Found entity: {file_path}")

                # 1. Trích xuất tên Table chính xác (bỏ qua Index/UniqueConstraints nếu có)
                table_match = re.search(r'@Table\s*\(\s*name\s*=\s*"([^"]+)"', java_content)
                if not table_match:
                    # Giao thức fallback 2 nếu có thuộc tính khác đứng trước name
                    table_match = re.search(r'@Table\s*\([^)]*?\bname\s*=\s*"([^"]+)"', java_content)

                if table_match:
                    table_name = table_match.group(1).strip()
                else:
                    # Lấy tên Class làm tên Table mặc định nếu không khai báo @Table
                    class_match = re.search(r'public\s+class\s+([a-zA-Z0-9_]+)', java_content)
                    table_name = class_match.group(1).lower().strip() if class_match else None

                if table_name:
                    print(f"   [Table Name]: {table_name}")

                    # 2. Đồng bộ Comment của TABLE
                    table_comment_match = re.search(r'@Table\s*\([^)]*?\bcomment\s*=\s*"([^"]+)"', java_content)
                    if table_comment_match:
                        table_comment = table_comment_match.group(1).replace("'", "''").strip()
                        print(f"   [Table Comment]: {table_comment}")

                        # Regex tìm khối CREATE TABLE tương ứng để thay thế COMMENT='...'
                        table_regex = rf"(CREATE TABLE\s+`?{table_name}`?.*?;)"
                        # Tìm và update comment của Table trong schema_content
                        # (Mẹo này tùy thuộc vào cấu trúc schema.sql của bạn)

                    # 3. Tìm toàn bộ các block @Column(...)
                    column_blocks = re.findall(r'@Column\s*\(([^)]+)\)', java_content)
                    for block in column_blocks:
                        col_name_match = re.search(r'\bname\s*=\s*"([^"]+)"', block)
                        col_comment_match = re.search(r'\bcomment\s*=\s*"([^"]+)"', block)

                        if col_name_match and col_comment_match:
                            col_name = col_name_match.group(1).strip()
                            col_comment = col_comment_match.group(1).replace("'", "''").strip()
                            print(f"     ↳ Found Col: {col_name} -> Comment: {col_comment}")

                            # Xử lý cập nhật chuỗi regex trực tiếp trong biến schema_content
                            # Tìm block bảng -> tìm dòng chứa cột -> sửa COMMENT
                            pattern = rf"(CREATE TABLE\s+`?{table_name}`?[\s\S]*?`?{col_name}`?[\s\S]*?COMMENT\s+')[^']*(')"
                            if re.search(pattern, schema_content, re.IGNORECASE):
                                schema_content = re.sub(pattern, rf"\1{col_comment}\2", schema_content, flags=re.IGNORECASE)
                            else:
                                # Thử format nháy kép hoặc dấu =
                                pattern_alt = rf"(CREATE TABLE\s+`?{table_name}`?[\s\S]*?`?{col_name}`?[\s\S]*?COMMENT\s*=\s*')[^']*(')"
                                schema_content = re.sub(pattern_alt, rf"\1{col_comment}\2", schema_content, flags=re.IGNORECASE)
                else:
                    print("   ⚠️ Cảnh báo: Hoàn toàn không thể xác định được tên Table")

# Ghi lại nội dung đã đồng bộ hoàn chỉnh vào file schema.sql
with open(SCHEMA_FILE, "w", encoding="utf-8") as f:
    f.write(schema_content)

print("========================================"
print("=== Sync completed successfully! ===")
