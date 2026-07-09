import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormContext } from 'react-hook-form';
import { FormSelect } from '../../components/FormSelect';
import { FormRadioGroup } from '../../components/FormRadioGroup';

// ステップ4：補償内容コンポーネント。各賠償限度額および特約を処理する。そのうち、対人賠償は法定/業界の強制要件として通常「無制限」に固定されるため、ここでは設計意図を示すために読み取り専用の形式で表示している。
export const Step4: React.FC = () => {
  const navigate = useNavigate();
  const { trigger } = useFormContext();

  const handleNext = async () => {
    const isValid = await trigger(['propertyDamageLimit', 'personalInjuryAmount', 'lawyerOption', 'roadService']);
    if (isValid) navigate('/quote/confirm');
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24, color: 'var(--color-primary)' }}>補償内容の選択</h2>
      
      <div style={{ marginBottom: 24 }}>
        <label className="form-label">対人賠償</label>
        <div style={{ padding: '12px 16px', backgroundColor: '#F9FAFB', borderRadius: 8, color: '#6B7280' }}>
          無制限 (固定)
        </div>
      </div>

      <FormSelect name="propertyDamageLimit" label="対物賠償" options={[
        { value: 'UNLIMITED', label: '無制限' },
        { value: 'THIRTY_MILLION', label: '3000万円' },
      ]} />
      
      <FormSelect name="personalInjuryAmount" label="人身傷害" options={[
        { value: 'THIRTY_MILLION', label: '3000万円' },
        { value: 'FIFTY_MILLION', label: '5000万円' },
        { value: 'UNLIMITED', label: '無制限' },
      ]} />

      <FormRadioGroup name="lawyerOption" label="弁護士費用特約" options={[
        { value: true, label: 'つける' },
        { value: false, label: 'つけない' },
      ]} />

      <FormRadioGroup name="roadService" label="ロードサービス" options={[
        { value: true, label: 'つける' },
        { value: false, label: 'つけない' },
      ]} />

      <div className="button-group">
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/quote/step3')}>戻る</button>
        <button type="button" className="btn btn-primary" onClick={handleNext}>確認画面へ</button>
      </div>
    </div>
  );
};
