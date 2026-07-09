import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { FormProvider, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { step3Schema } from '../schemas/quoteSchema';
import { Step3 } from '../pages/steps/Step3';

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm({
    resolver: zodResolver(step3Schema),
    mode: 'onTouched',
    defaultValues: {
      vehicleInsurance: false
    }
  });

  return (
    <FormProvider {...methods}>
      <MemoryRouter initialEntries={['/quote/step3']}>
        <Routes>
          <Route path="/quote/step3" element={children} />
          <Route path="/quote/step4" element={<div data-testid="step4">Step 4</div>} />
        </Routes>
      </MemoryRouter>
    </FormProvider>
  );
};

describe('Step3 Vehicle Info', () => {
  it('ST-005: should show error when future date is input for registration', async () => {
    const user = userEvent.setup();
    render(<Step3 />, { wrapper: Wrapper });

    await user.type(screen.getByLabelText('メーカー'), 'トヨタ');
    await user.type(screen.getByLabelText('車名'), 'プリウス');
    await user.selectOptions(screen.getByLabelText('車両タイプ'), 'SEDAN');
    await user.click(screen.getByLabelText('付帯しない'));

    // Input future date
    const nextYear = new Date().getFullYear() + 1;
    await user.type(screen.getByLabelText('初度登録年月 (YYYY-MM)'), `${nextYear}-01`);
    
    const nextBtn = screen.getByText('次へ');
    await user.click(nextBtn);

    await waitFor(() => {
      expect(screen.getByText('未来の年月は入力できません')).toBeInTheDocument();
    });
    
    expect(screen.queryByTestId('step4')).not.toBeInTheDocument();
  });
});
