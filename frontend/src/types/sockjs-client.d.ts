declare module 'sockjs-client' {
  export default class SockJS {
    constructor(url: string, options?: any);
    send(data: string): void;
    close(): void;
    onopen: (() => void) | null;
    onclose: (() => void) | null;
    onmessage: ((event: { data: string }) => void) | null;
    onerror: ((event: any) => void) | null;
  }
}
