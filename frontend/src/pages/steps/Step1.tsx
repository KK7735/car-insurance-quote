import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormContext } from 'react-hook-form';
import { FormInput } from '../../components/FormInput';
import { FormSelect } from '../../components/FormSelect';
import { FormRadioGroup } from '../../components/FormRadioGroup';

// ステップ1：運転者情報コンポーネント。主に年齢、免許証の色、使用目的などの基本情報を収集する。次へをクリックすると、現在のフィールドの部分的なバリデーション（trigger）がトリガーされる。
export const Step1: React.FC = () => {
  const navigate = useNavigate();
  const { trigger } = useFormContext();

  const handleNext = async () => {
    const isValid = await trigger(['driverAge', 'licenseColor', 'usageType', 'annualMileage', 'driverRange']);
    if (isValid) navigate('/quote/step2');
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24, color: 'var(--color-primary)' }}>運転者情報</h2>
      <FormInput name="driverAge" label="運転者年齢" type="number" placeholder="例: 35" />
      <FormRadioGroup name="licenseColor" label="免許証の色" options={[
        { value: 'GREEN', label: 'グリーン' },
        { value: 'BLUE', label: 'ブルー' },
        { value: 'GOLD', label: 'ゴールド' },
      ]} />
      <FormSelect name="usageType" label="使用目的" options={[
        { value: 'PRIVATE', label: '日常・レジャー' },
        { value: 'COMMUTE', label: '通勤・通学' },
        { value: 'BUSINESS', label: '業務' },
      ]} />
      <FormInput name="annualMileage" label="年間走行距離 (km)" type="number" placeholder="例: 8000" />
      <FormSelect name="driverRange" label="運転者範囲" options={[
        { value: 'SELF', label: '本人のみ' },
        { value: 'COUPLE', label: '本人・配偶者' },
        { value: 'FAMILY', label: '同居の親族' },
        { value: 'ANYONE', label: '限定なし' },
      ]} />
      <div className="button-group" style={{ justifyContent: 'flex-end' }}>
        <button type="button" className="btn btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
};
