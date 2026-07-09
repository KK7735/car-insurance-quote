import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter, MemoryRouter, Routes, Route } from 'react-router-dom';
import AdminLogin from '../pages/admin/AdminLogin';
import AdminQuoteList from '../pages/admin/AdminQuoteList';
import adminAxios from '../utils/adminAxios';
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock('../utils/adminAxios', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}));

describe('ST-010: Admin Flow', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows validation errors when submitting empty form', async () => {
    const user = userEvent.setup();
    render(
      <BrowserRouter>
        <AdminLogin />
      </BrowserRouter>
    );

    await user.click(screen.getByRole('button', { name: 'ログイン' }));

    await waitFor(() => {
      expect(screen.getByText('ユーザー名を入力してください')).toBeInTheDocument();
      expect(screen.getByText('パスワードを入力してください')).toBeInTheDocument();
    });
  });

  it('ST-010: should login, display quote list, and perform search', async () => {
    const user = userEvent.setup();
    const mockPost = vi.mocked(adminAxios.post);
    const mockGet = vi.mocked(adminAxios.get);

    // Mock Login
    mockPost.mockResolvedValueOnce({ data: { token: 'fake-token' } });

    // Mock Initial Quote List
    mockGet.mockResolvedValueOnce({
      data: {
        content: [
          { quoteNo: 'EST202601010001', driverAge: 30, carName: 'PRIUS', annualPremium: 50000, createdAt: '2026-01-01T10:00:00Z' },
          { quoteNo: 'EST202601010002', driverAge: 40, carName: 'FIT', annualPremium: 40000, createdAt: '2026-01-01T11:00:00Z' }
        ],
        totalPages: 1,
        number: 0
      }
    });

    // Mock Search Result
    mockGet.mockResolvedValueOnce({
      data: {
        content: [
          { quoteNo: 'EST202601010001', driverAge: 30, carName: 'PRIUS', annualPremium: 50000, createdAt: '2026-01-01T10:00:00Z' }
        ],
        totalPages: 1,
        number: 0
      }
    });

    render(
      <MemoryRouter initialEntries={['/admin/login']}>
        <Routes>
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin/quotes" element={<AdminQuoteList />} />
        </Routes>
      </MemoryRouter>
    );

    // --- Login ---
    await user.type(screen.getByLabelText('ユーザー名'), 'admin');
    await user.type(screen.getByLabelText('パスワード'), 'password');
    await user.click(screen.getByRole('button', { name: 'ログイン' }));

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/login', { username: 'admin', password: 'password' });
    });

    // Wait for redirect to /admin/quotes and initial list render
    await waitFor(() => {
      expect(screen.getByText('見積一覧')).toBeInTheDocument();
      expect(screen.getByText('EST202601010001')).toBeInTheDocument();
      expect(screen.getByText('EST202601010002')).toBeInTheDocument();
    });

    // --- Search ---
    const searchInput = screen.getByPlaceholderText('見積番号で検索');
    await user.type(searchInput, 'EST202601010001');
    await user.click(screen.getByRole('button', { name: '検索' }));

    // Check search params and results
    await waitFor(() => {
      expect(mockGet).toHaveBeenCalledWith('/quotes', {
        params: { page: 0, size: 10, quoteNo: 'EST202601010001' }
      });
      // Second quote should be filtered out
      expect(screen.queryByText('EST202601010002')).not.toBeInTheDocument();
      expect(screen.getByText('EST202601010001')).toBeInTheDocument();
    });
  });
});
