import React from 'react';
import { useNavigate } from 'react-router-dom';

export const TopPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="app-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div className="card" style={{ textAlign: 'center', padding: '60px 40px', marginTop: '10vh' }}>
        <h1 style={{ color: 'var(--color-primary)', marginBottom: 24, fontSize: 32 }}>自動車保険お見積り</h1>
        <p style={{ color: 'var(--color-text-muted)', marginBottom: 48, fontSize: 16 }}>
          たった数ステップで、あなたにぴったりの自動車保険料を計算します。<br />
          しつこい勧誘は一切ありません。
        </p>
        <button className="btn btn-primary" style={{ fontSize: 18, padding: '16px 48px' }} onClick={() => navigate('/quote/step1')}>
          見積もりを始める
        </button>
      </div>
    </div>
  );
};
