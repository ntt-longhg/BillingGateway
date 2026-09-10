import React from 'react';
import { Outlet } from 'react-router-dom';
import { useIframeResize } from '../hooks/useIframeResize';
import { usePostMessageListener } from '../hooks/usePostMessageListener';

/**
 * Layout Embed dành cho iFrame Client.
 * RỖNG HOÀN TOÀN (Không Header, Sidebar, Footer).
 * Hiển thị Edge-to-Edge và tự động điều khiển postMessage.
 */
export const EmbedLayout: React.FC = () => {
  // Tự động gửi tín hiệu RESIZE ra trang cha khi chiều cao thay đổi
  useIframeResize();

  // Lắng nghe tín hiệu NAVIGATE từ menu trang cha
  usePostMessageListener();

  return (
    <div
      id="embed-root-container"
      className="w-full min-h-screen m-0 p-4 box-border bg-transparent font-sans text-slate-900"
    >
      <Outlet />
    </div>
  );
};
