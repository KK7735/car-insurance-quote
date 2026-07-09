import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { FormProvider, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { step2Schema } from '../schemas/quoteSchema';
import { Step2 } from '../pages/steps/Step2';

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm({
    resolver: zodResolver(step2Schema),
    mode: 'onTouched',
    defaultValues: {
      hasCurrentInsurance: false
    }
  });

  return (
    <FormProvider {...methods}>
      <MemoryRouter initialEntries={['/quote/step2']}>
        <Routes>
          <Route path="/quote/step2" element={children} />
          <Route path="/quote/step3" element={<div data-testid="step3">Step 3</div>} />
        </Routes>
      </MemoryRouter>
    </FormProvider>
  );
};

describe('Step2CurrentInsurance', () => {
  it('ST-004: should require grade and accidentTerm when hasCurrentInsurance is true', async () => {
    const user = userEvent.setup();
    render(<Step2 />, { wrapper: Wrapper });

    // 1. 初始状态（加入していない）下，没有额外的输入框
    expect(screen.queryByLabelText('現在の等級 (1-20)')).not.toBeInTheDocument();

    // 2. 点击 "加入している"
    const radioTrue = screen.getByLabelText('加入している');
    await user.click(radioTrue);

    // 断言输入框出现
    expect(screen.getByLabelText('現在の等級 (1-20)')).toBeInTheDocument();
    expect(screen.getByLabelText('事故有係数適用期間 (0-6)')).toBeInTheDocument();

    // 3. 不填写数据直接点击下一步
    const nextBtn = screen.getByText('次へ');
    await user.click(nextBtn);

    // 断言必须展示错误提示
    await waitFor(() => {
      expect(screen.getByText('現在加入ありの場合、等級(1-20)は必須です')).toBeInTheDocument();
      expect(screen.getByText('現在加入ありの場合、事故有係数期間(0-6)は必須です')).toBeInTheDocument();
    });

    // 4. 取消勾选（退回到加入していない）
    const radioFalse = screen.getByLabelText('加入していない');
    await user.click(radioFalse);

    // 断言输入框消失，且后续点击可以正常跳转
    expect(screen.queryByLabelText('現在の等級 (1-20)')).not.toBeInTheDocument();
    await user.click(nextBtn);
    await waitFor(() => {
      expect(screen.getByTestId('step3')).toBeInTheDocument();
    });
  });
});
