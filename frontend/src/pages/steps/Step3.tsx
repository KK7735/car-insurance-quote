import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormContext } from 'react-hook-form';
import { FormInput } from '../../components/FormInput';
import { FormSelect } from '../../components/FormSelect';
import { FormRadioGroup } from '../../components/FormRadioGroup';

// ステップ3：車両情報コンポーネント。車種、初度登録年月など、基本保険料を決定する重要な車両要因を収集する。
export const Step3: React.FC = () => {
  const navigate = useNavigate();
  const { trigger } = useFormContext();

  const handleNext = async () => {
    const isValid = await trigger(['maker', 'carName', 'firstRegistrationYearMonth', 'vehicleType', 'vehicleInsurance']);
    if (isValid) navigate('/quote/step4');
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24, color: 'var(--color-primary)' }}>お車の情報</h2>
      <FormInput name="maker" label="メーカー" placeholder="例: トヨタ" />
      <FormInput name="carName" label="車名" placeholder="例: プリウス" />
      <FormInput name="firstRegistrationYearMonth" label="初度登録年月 (YYYY-MM)" placeholder="例: 2024-01" />
      <FormSelect name="vehicleType" label="車両タイプ" options={[
        { value: 'COMPACT', label: 'コンパクト' },
        { value: 'SEDAN', label: 'セダン' },
        { value: 'MINIVAN', label: 'ミニバン' },
        { value: 'SUV', label: 'SUV' },
        { value: 'KEI', label: '軽自動車' },
      ]} />
      <FormRadioGroup name="vehicleInsurance" label="車両保険を付帯しますか？" options={[
        { value: true, label: '付帯する' },
        { value: false, label: '付帯しない' },
      ]} />

      <div className="button-group">
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/quote/step2')}>戻る</button>
        <button type="button" className="btn btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
};
