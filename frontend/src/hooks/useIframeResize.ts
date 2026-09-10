import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

/**
 * Custom Hook theo dõi sự thay đổi chiều cao nội dung iFrame và gửi tin nhắn RESIZE ra trang cha
 */
export const useIframeResize = () => {
  const location = useLocation();

  useEffect(() => {
    const sendHeight = () => {
      if (window.parent && window.parent !== window) {
        // Lấy chiều cao chuẩn của document
        const height = Math.max(
          document.documentElement.scrollHeight,
          document.body.scrollHeight,
          document.documentElement.offsetHeight
        );
        
        window.parent.postMessage(
          {
            action: 'RESIZE',
            height: height,
            path: location.pathname,
          },
          '*'
        );
      }
    };

    // Gửi ngay lập tức khi load/chuyển route
    sendHeight();

    // Theo dõi DOM thay đổi khi có API render thêm dữ liệu
    const observer = new ResizeObserver(() => {
      sendHeight();
    });

    observer.observe(document.body);
    observer.observe(document.documentElement);

    // Timeout để đảm bảo nhận đúng chiều cao sau khi hình ảnh/font đã nạp xong
    const timer1 = setTimeout(sendHeight, 100);
    const timer2 = setTimeout(sendHeight, 500);

    return () => {
      observer.disconnect();
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, [location.pathname]);
};
