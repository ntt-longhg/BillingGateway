import { useEffect, useRef, useCallback, useState } from 'react';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface UseWebSocketOptions {
  tenantId?: string;
  admin?: boolean;
  onNotification?: (notification: any) => void;
  enabled?: boolean;
}

export function useWebSocket({ tenantId, admin, onNotification, enabled = true }: UseWebSocketOptions) {
  const clientRef = useRef<Client | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const [lastNotification, setLastNotification] = useState<any>(null);

  const connect = useCallback(() => {
    if (!enabled) return;
    if (!tenantId && !admin) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log("Connected to WebSocket", admin ? "(admin)" : `(tenant=${tenantId})`);
        setIsConnected(true);

        if (admin) {
          // Admin subscribes to all admin notifications
          client.subscribe('/topic/admin/notifications', (message: IMessage) => {
            try {
              const notification = JSON.parse(message.body);
              setLastNotification(notification);
              onNotification?.(notification);
            } catch (e) {
              console.error('Failed to parse admin notification:', e);
            }
          });
        } else if (tenantId) {
          // Tenant subscribes to tenant-specific notifications
          client.subscribe(`/topic/notifications/${tenantId}`, (message: IMessage) => {
            try {
              const notification = JSON.parse(message.body);
              setLastNotification(notification);
              onNotification?.(notification);
            } catch (e) {
              console.error('Failed to parse notification:', e);
            }
          });
        }
      },
      onDisconnect: () => {
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message']);
        setIsConnected(false);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
      setIsConnected(false);
    };
  }, [tenantId, admin, onNotification, enabled]);

  useEffect(() => {
    const cleanup = connect();
    return () => cleanup?.();
  }, [connect]);

  return { isConnected, lastNotification };
}
