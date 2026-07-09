import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { FormProvider, useForm } from 'react-hook-form';
import { Confirm } from '../pages/steps/Confirm';
import { Step4 } from '../pages/steps/Step4';
import { quoteApi } from '../api/client';

vi.mock('../api/client', () => ({
  quoteApi: {
    createQuote: vi.fn(),
  },
}));

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm({
    defaultValues: {
      driverAge: 35,
      hasCurrentInsurance: false,
      maker: 'TOYOTA',
      carName: 'PRIUS',
      propertyDamageLimit: 'UNLIMITED'
    }
  });

  return (
    <FormProvider {...methods}>
      <MemoryRouter initialEntries={['/quote/confirm']}>
        <Routes>
          <Route path="/quote/confirm" element={children} />
          <Route path="/quote/step4" element={<Step4 />} />
          <Route path="/result/:quoteNo" element={<div data-testid="result">Result Page</div>} />
        </Routes>
      </MemoryRouter>
    </FormProvider>
  );
};

describe('Confirm Page', () => {
  it('ST-007: should preserve data and go back to step4 when clicking back button', async () => {
    const user = userEvent.setup();
    render(<Confirm />, { wrapper: Wrapper });

    expect(screen.getByText(/35 歳/)).toBeInTheDocument();

    const backBtn = screen.getByText('内容を修正する');
    await user.click(backBtn);

    await waitFor(() => {
      const select = screen.getByLabelText('対物賠償') as HTMLSelectElement;
      expect(select.value).toBe('UNLIMITED');
    });
  });

  it('ST-008: should submit quote and navigate to result page', async () => {
    const user = userEvent.setup();
    vi.mocked(quoteApi.createQuote).mockResolvedValueOnce({
      quoteNo: 'EST202612340001',
      annualPremium: 50000,
      monthlyPremium: 4300,
      breakdowns: [],
      createdAt: ''
    });

    render(<Confirm />, { wrapper: Wrapper });

    const submitBtn = screen.getByText('見積もりを作成する');
    await user.click(submitBtn);

    await waitFor(() => {
      expect(quoteApi.createQuote).toHaveBeenCalled();
      expect(screen.getByTestId('result')).toBeInTheDocument();
    });
  });
});
