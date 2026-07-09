import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormContext } from 'react-hook-form';
import { FormInput } from '../../components/FormInput';
import { FormRadioGroup } from '../../components/FormRadioGroup';

// ステップ2：現在の保険加入状況コンポーネント。React Hook Form の watch メカニズムに基づく動的な条件付きレンダリング（保険加入済みを選択した場合のみ、等級入力ボックスが展開される）のデモである。
export const Step2: React.FC = () => {
  const navigate = useNavigate();
  const { trigger, watch } = useFormContext();
  const hasCurrentInsurance = watch('hasCurrentInsurance');
  const isInsuranceActive = hasCurrentInsurance === true || hasCurrentInsurance === 'true';

  const handleNext = async () => {
    const isValid = await trigger(['hasCurrentInsurance', 'grade', 'accidentTerm']);
    if (isValid) navigate('/quote/step3');
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24, color: 'var(--color-primary)' }}>現在の加入状況</h2>
      <FormRadioGroup name="hasCurrentInsurance" label="現在、自動車保険に加入していますか？" options={[
        { value: true, label: '加入している' },
        { value: false, label: '加入していない' },
      ]} />
      
      {isInsuranceActive && (
        <div style={{ marginTop: 24, padding: 24, backgroundColor: '#F9FAFB', borderRadius: 8 }}>
          <FormInput name="grade" label="現在の等級 (1-20)" type="number" placeholder="例: 20" />
          <FormInput name="accidentTerm" label="事故有係数適用期間 (0-6)" type="number" placeholder="例: 0" />
        </div>
      )}

      <div className="button-group">
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/quote/step1')}>戻る</button>
        <button type="button" className="btn btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
};
