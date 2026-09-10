import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface PostMessageData {
  action: string;
  to?: string;
  [key: string]: any;
}

/**
 * Custom Hook lắng nghe sự kiện message từ trang cha để chuyển hướng iFrame
 */
export const usePostMessageListener = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const handleMessage = (event: MessageEvent<PostMessageData>) => {
      // Kiểm tra tính hợp lệ của dữ liệu nhận vào
      if (!event.data || typeof event.data !== 'object') return;

      const { action, to } = event.data;

      // Xử lý sự kiện điều hướng NAVIGATE từ menu trang cha
      if (action === 'NAVIGATE' && to && typeof to === 'string') {
        navigate(to);
      }
    };

    window.addEventListener('message', handleMessage);

    return () => {
      window.removeEventListener('message', handleMessage);
    };
  }, [navigate]);
};
