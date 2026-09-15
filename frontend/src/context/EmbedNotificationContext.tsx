import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import { useToast } from '../components/ui/toast';
import { embedService } from '../services/billingServices';

interface Notification {
  id: string;
  tenantId: string;
  type: string;
  title: string;
  message: string;
  referenceType?: string;
  referenceId?: string;
  isRead: boolean;
  readAt?: string;
  createdAt: string;
}

interface EmbedNotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  isConnected: boolean;
}

const EmbedNotificationContext = createContext<EmbedNotificationContextType>({
  notifications: [],
  unreadCount: 0,
  isConnected: false,
});

export const EmbedNotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [tenantId, setTenantId] = useState<string | null>(null);
  const { addToast } = useToast();

  useEffect(() => {
    const fetchTenantId = async () => {
      try {
        const res = await embedService.getTenantInfo();
        if (res.data.success && res.data.data) {
          setTenantId(res.data.data.tenantId);
        }
      } catch {
        // ignore - will work without notifications
      }
    };
    fetchTenantId();
  }, []);

  const handleNotification = useCallback((notification: Notification) => {
    setNotifications(prev => [notification, ...prev].slice(0, 50));
    addToast({
      variant: 'info',
      title: notification.title,
      message: notification.message,
      duration: 5000,
    });
  }, [addToast]);

  const { isConnected } = useWebSocket({
    tenantId: tenantId || undefined,
    onNotification: handleNotification,
    enabled: !!tenantId,
  });

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <EmbedNotificationContext.Provider value={{ notifications, unreadCount, isConnected }}>
      {children}
    </EmbedNotificationContext.Provider>
  );
};

export const useEmbedNotifications = () => useContext(EmbedNotificationContext);
