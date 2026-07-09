import React, { useEffect } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useForm, FormProvider } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { quoteSchema, QuoteFormValues } from '../schemas/quoteSchema';

const steps = [
  { path: '/quote/step1', label: '運転者' },
  { path: '/quote/step2', label: '現在加入' },
  { path: '/quote/step3', label: '車両' },
  { path: '/quote/step4', label: '補償' },
  { path: '/quote/confirm', label: '確認' },
];

// マルチステップウィザードのレイアウトコンポーネントであり、見積もりプロセス全体の状態マシンコンテナでもある。react-hook-form を使用してすべてのステップのデータフローを維持し、FormProvider を通じて配信する。
export const WizardLayout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const currentStepIndex = steps.findIndex(s => s.path === location.pathname);

  const methods = useForm<QuoteFormValues>({
    // @ts-expect-error ZodIntersection with ZodEffects typing issue in react-hook-form
    resolver: zodResolver(quoteSchema),
    mode: 'onTouched',
    defaultValues: {
      hasCurrentInsurance: false,
      vehicleInsurance: false,
      lawyerOption: false,
      roadService: false,
    }
  });

  useEffect(() => {
    // ルートガード：ページ更新による状態の喪失を検知する。編集の痕跡がない場合は、必須項目の欠落による異常なフローを防ぐため、強制的に最初のステップに戻す。
    if (currentStepIndex > 0 && !methods.formState.isDirty && Object.keys(methods.formState.touchedFields).length === 0) {
      navigate('/quote/step1', { replace: true });
    }
  }, [currentStepIndex, methods, navigate]);

  return (
    <div className="app-container">
      <div className="card">
        <div className="stepper">
          {steps.map((step, index) => {
            const isActive = index === currentStepIndex;
            const isCompleted = index < currentStepIndex;
            return (
              <div key={step.path} className={`step-item ${isActive ? 'active' : ''} ${isCompleted ? 'completed' : ''}`}>
                <div className="step-circle">{index + 1}</div>
                <div className="step-title">{step.label}</div>
              </div>
            );
          })}
        </div>
        <FormProvider {...methods}>
          {/* ここの Outlet は現在のURLに基づいて具体的なサブステップページをレンダリングし、サブページは useFormContext を通じて methods を取得する。key はルート切り替え時にアニメーションがトリガーされることを保証する。 */}
          <div key={location.pathname} className="fade-in">
            <Outlet />
          </div>
        </FormProvider>
      </div>
    </div>
  );
};
