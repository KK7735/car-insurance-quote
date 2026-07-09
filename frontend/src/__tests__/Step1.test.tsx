import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { FormProvider, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { step1Schema } from '../schemas/quoteSchema';
import { Step1 } from '../pages/steps/Step1';

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm({
    resolver: zodResolver(step1Schema),
    mode: 'onTouched',
    defaultValues: {
      driverAge: undefined,
      annualMileage: undefined
    }
  });

  return (
    <FormProvider {...methods}>
      <MemoryRouter initialEntries={['/quote/step1']}>
        <Routes>
          <Route path="/quote/step1" element={children} />
          <Route path="/quote/step2" element={<div data-testid="step2">Step 2</div>} />
        </Routes>
      </MemoryRouter>
    </FormProvider>
  );
};

describe('Step1 Driver Info', () => {
  it('ST-002: should show validation errors when submitting empty form', async () => {
    const user = userEvent.setup();
    render(<Step1 />, { wrapper: Wrapper });

    const nextBtn = screen.getByText('次へ');
    await user.click(nextBtn);

    await waitFor(() => {
      expect(screen.getAllByText('18 以上の値にしてください').length).toBeGreaterThan(0);
      expect(screen.getAllByText('必須項目です').length).toBeGreaterThan(0);
      expect(screen.getAllByText('数値を入力してください').length).toBeGreaterThan(0);
    });
    
    // Should not navigate
    expect(screen.queryByTestId('step2')).not.toBeInTheDocument();
  });

  it('ST-003: should navigate to step2 when form is valid', async () => {
    const user = userEvent.setup();
    render(<Step1 />, { wrapper: Wrapper });

    await user.type(screen.getByLabelText('運転者年齢'), '35');
    await user.click(screen.getByLabelText('ゴールド'));
    await user.selectOptions(screen.getByLabelText('使用目的'), 'PRIVATE');
    await user.type(screen.getByLabelText('年間走行距離 (km)'), '8000');
    await user.selectOptions(screen.getByLabelText('運転者範囲'), 'SELF');

    const nextBtn = screen.getByText('次へ');
    await user.click(nextBtn);

    await waitFor(() => {
      expect(screen.getByTestId('step2')).toBeInTheDocument();
    });
  });
});
