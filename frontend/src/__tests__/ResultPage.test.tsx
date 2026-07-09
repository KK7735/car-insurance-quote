import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { ResultPage } from '../pages/ResultPage';
import { quoteApi } from '../api/client';

vi.mock('../api/client', () => ({
  quoteApi: {
    getQuote: vi.fn(),
  },
}));

describe('ResultPage', () => {
  it('ST-009: should fetch and display quote details correctly', async () => {
    vi.mocked(quoteApi.getQuote).mockResolvedValueOnce({
      quoteNo: 'EST202612340001',
      annualPremium: 50000,
      monthlyPremium: 4300,
      breakdowns: [
        { itemName: '年齢条件', amount: 10000, rate: null },
        { itemName: '車両保険', amount: null, rate: 1.5 }
      ],
      createdAt: '2026-07-07T00:00:00.000Z'
    });

    render(
      <MemoryRouter initialEntries={['/result/EST202612340001']}>
        <Routes>
          <Route path="/result/:quoteNo" element={<ResultPage />} />
        </Routes>
      </MemoryRouter>
    );

    // Should show loading initially
    expect(screen.getByText('読み込み中...')).toBeInTheDocument();

    await waitFor(() => {
      // Check quoteNo
      expect(screen.getByText('見積番号: EST202612340001')).toBeInTheDocument();
      // Check premiums
      expect(screen.getByText('50,000')).toBeInTheDocument();
      expect(screen.getByText('（月額: 4,300 円）')).toBeInTheDocument();
      // Check breakdowns
      expect(screen.getByText('年齢条件')).toBeInTheDocument();
      expect(screen.getByText('+ 10,000 円')).toBeInTheDocument();
      expect(screen.getByText('車両保険')).toBeInTheDocument();
      expect(screen.getByText('× 1.500')).toBeInTheDocument();
    });
  });
});
