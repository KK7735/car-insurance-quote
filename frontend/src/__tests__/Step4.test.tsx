import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { FormProvider, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { step4Schema } from '../schemas/quoteSchema';
import { Step4 } from '../pages/steps/Step4';

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm({
    resolver: zodResolver(step4Schema),
    mode: 'onTouched',
  });

  return (
    <FormProvider {...methods}>
      <MemoryRouter initialEntries={['/quote/step4']}>
        <Routes>
          <Route path="/quote/step4" element={children} />
          <Route path="/quote/confirm" element={<div data-testid="confirm">Confirm</div>} />
        </Routes>
      </MemoryRouter>
    </FormProvider>
  );
};

describe('Step4 Compensation Options', () => {
  it('ST-006: should navigate to confirm page when valid options are selected', async () => {
    const user = userEvent.setup();
    render(<Step4 />, { wrapper: Wrapper });

    await user.selectOptions(screen.getByLabelText('対物賠償'), 'UNLIMITED');
    await user.selectOptions(screen.getByLabelText('人身傷害'), 'FIFTY_MILLION');
    
    // Choose true for lawyer and road service (first and second 'つける')
    const options = screen.getAllByLabelText('つける');
    await user.click(options[0]);
    await user.click(options[1]);

    const nextBtn = screen.getByText('確認画面へ');
    await user.click(nextBtn);

    await waitFor(() => {
      expect(screen.getByTestId('confirm')).toBeInTheDocument();
    });
  });
});
