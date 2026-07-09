import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormContext } from 'react-hook-form';
import { quoteApi } from '../../api/client';
import { QuoteFormValues } from '../../schemas/quoteSchema';

const UserIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
    <circle cx="12" cy="7" r="4"></circle>
  </svg>
);

const ShieldIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
    <path d="M9 12l2 2 4-4"></path>
  </svg>
);

const CarIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M14 16H9m10 0h3v-3.15a1 1 0 0 0-.84-.99L16 11l-2.7-3.6a2 2 0 0 0-1.6-.8H9.3a2 2 0 0 0-1.6.8L5 11l-5.16.86a1 1 0 0 0-.84.99V16h3m10 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm-10 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4z"></path>
  </svg>
);

const CardHeader: React.FC<{ icon: React.ReactNode; title: string }> = ({ icon, title }) => (
  <div style={{ display: 'flex', alignItems: 'center', padding: '16px 24px', borderBottom: '1px solid #E5E7EB', backgroundColor: '#FFFFFF' }}>
    <div style={{ backgroundColor: '#0F766E', color: '#FFFFFF', width: '32px', height: '32px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginRight: '12px' }}>
      {icon}
    </div>
    <h3 style={{ fontSize: '18px', fontWeight: 'bold', color: '#0F766E', margin: 0 }}>{title}</h3>
  </div>
);

const FieldValue: React.FC<{ label: string; value: React.ReactNode; testId: string }> = ({ label, value, testId }) => (
  <div>
    <div style={{ fontSize: '13px', color: '#6B7280', marginBottom: '4px' }}>{label}</div>
    <div data-testid={testId} style={{ fontSize: '15px', color: '#111827' }}>{value}</div>
  </div>
);

// 確認画面コンポーネント。これはフォーム送信前の最後の防衛線である。コンテキストで収集されたすべての列挙値とブール値をユーザーフレンドリーな日本語テキストに変換し、API を呼び出して送信する役割を担う。
export const Confirm: React.FC = () => {
  const navigate = useNavigate();
  const { getValues } = useFormContext<QuoteFormValues>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const values = getValues();

  const handleSubmit = async () => {
    try {
      // 送信時にエラー状態をリセットする。API が 400 Bad Request を返した場合（例：バックエンドのバリデーション失敗）、ここでキャッチされ、具体的なフィールドのエラー情報が表示される。
      setLoading(true);
      setError(null);
      const res = await quoteApi.createQuote(values);
      navigate(`/result/${res.quoteNo}`);
    } catch (e: any) {
      if (e.response?.data?.details) {
        setError(JSON.stringify(e.response.data.details));
      } else {
        setError(e.response?.data?.message || 'システムエラーが発生しました');
      }
      setLoading(false);
    }
  };

  const cardStyle = { backgroundColor: '#FFFFFF', borderRadius: '8px', border: '1px solid #E5E7EB', marginBottom: '24px', overflow: 'hidden' };
  const gridStyle = { padding: '24px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '24px' };

  return (
    <div>
      <h2 style={{ marginBottom: 8, color: 'var(--color-primary)' }}>入力内容の確認</h2>
      <p style={{ fontSize: '14px', color: '#4B5563', marginBottom: '24px' }}>入力内容をご確認ください。修正が必要な場合は「内容を修正する」ボタンを押してください。</p>
      
      {error && (
        <div style={{ color: 'var(--color-error)', padding: 16, backgroundColor: '#FEF2F2', borderRadius: 8, marginBottom: 24 }}>
          {error}
        </div>
      )}

      <div style={cardStyle}>
        <CardHeader icon={<UserIcon />} title="1. 使用者情報" />
        <div style={gridStyle}>
          <FieldValue testId="field-driverAge" label="運転者年齢" value={`${values.driverAge} 歳`} />
          <FieldValue testId="field-licenseColor" label="免許証の色" value={{ GOLD: 'ゴールド', BLUE: 'ブルー', GREEN: 'グリーン' }[values.licenseColor]} />
          <FieldValue testId="field-usageType" label="使用目的" value={{ PRIVATE: '日常・レジャー', COMMUTE: '通勤・通学', BUSINESS: '業務' }[values.usageType]} />
          <FieldValue testId="field-annualMileage" label="年間走行距離" value={`${values.annualMileage} km`} />
          <FieldValue testId="field-driverRange" label="運転者範囲" value={{ SELF: '本人のみ', COUPLE: '本人・配偶者', FAMILY: '同居の親族', ANYONE: '限定なし' }[values.driverRange]} />
        </div>
      </div>

      <div style={cardStyle}>
        <CardHeader icon={<ShieldIcon />} title="2. 契約中保険" />
        <div style={gridStyle}>
          <FieldValue testId="field-hasCurrentInsurance" label="現在加入有無" value={values.hasCurrentInsurance ? 'あり' : 'なし'} />
          {values.hasCurrentInsurance && (
            <>
              <FieldValue testId="field-grade" label="等級" value={`${values.grade} 等級`} />
              <FieldValue testId="field-accidentTerm" label="事故有係数期間" value={`${values.accidentTerm} 年`} />
            </>
          )}
        </div>
      </div>

      <div style={cardStyle}>
        <CardHeader icon={<CarIcon />} title="3. 車両情報" />
        <div style={gridStyle}>
          <FieldValue testId="field-maker" label="メーカー" value={values.maker} />
          <FieldValue testId="field-carName" label="車名" value={values.carName} />
          <FieldValue testId="field-firstRegistrationYearMonth" label="初度登録年月" value={values.firstRegistrationYearMonth} />
          <FieldValue testId="field-vehicleType" label="車両タイプ" value={{ COMPACT: 'コンパクト', SEDAN: 'セダン', MINIVAN: 'ミニバン', SUV: 'SUV', KEI: '軽自動車' }[values.vehicleType]} />
          <FieldValue testId="field-vehicleInsurance" label="車両保険" value={values.vehicleInsurance ? 'あり' : 'なし'} />
        </div>
      </div>

      <div style={cardStyle}>
        <CardHeader icon={<ShieldIcon />} title="4. 補償条件" />
        <div style={gridStyle}>
          <FieldValue testId="field-bodilyInjury" label="対人賠償" value="無制限" />
          <FieldValue testId="field-propertyDamageLimit" label="対物賠償" value={{ UNLIMITED: '無制限', THIRTY_MILLION: '3000万円' }[values.propertyDamageLimit]} />
          <FieldValue testId="field-personalInjuryAmount" label="人身傷害" value={{ THIRTY_MILLION: '3000万円', FIFTY_MILLION: '5000万円', UNLIMITED: '無制限' }[values.personalInjuryAmount]} />
          <FieldValue testId="field-lawyerOption" label="弁護士特約" value={values.lawyerOption ? 'あり' : 'なし'} />
          <FieldValue testId="field-roadService" label="ロードサービス" value={values.roadService ? 'あり' : 'なし'} />
        </div>
      </div>

      <div className="button-group">
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/quote/step4')} disabled={loading}>内容を修正する</button>
        <button type="button" className="btn btn-primary" onClick={handleSubmit} disabled={loading}>
          {loading ? '計算中...' : '見積もりを作成する'}
        </button>
      </div>
    </div>
  );
};
