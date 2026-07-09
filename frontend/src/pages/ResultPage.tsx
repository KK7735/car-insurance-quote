import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { quoteApi } from '../api/client';
import { QuoteResponse } from '../api/types';

export const ResultPage: React.FC = () => {
  const { quoteNo } = useParams();
  const navigate = useNavigate();
  const [result, setResult] = useState<QuoteResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (quoteNo) {
      quoteApi.getQuote(quoteNo)
        .then(setResult)
        .catch(() => navigate('/'))
        .finally(() => setLoading(false));
    }
  }, [quoteNo, navigate]);

  if (loading) {
    return <div className="app-container"><div className="card" style={{ textAlign: 'center' }}>読み込み中...</div></div>;
  }
  if (!result) return null;

  return (
    <div className="app-container">
      <div className="card">
        <h2 style={{ color: 'var(--color-primary)', textAlign: 'center' }}>お見積り結果</h2>
        <p style={{ textAlign: 'center', color: 'var(--color-text-muted)' }}>見積番号: {result.quoteNo}</p>
        
        <div style={{ marginTop: 40, padding: 40, backgroundColor: '#EFF6FF', borderRadius: 12, textAlign: 'center' }}>
          <div style={{ fontSize: 16, color: '#1E3A8A' }}>年間保険料</div>
          <div style={{ fontSize: 48, fontWeight: 'bold', color: '#1E3A8A', margin: '8px 0' }}>
            {result.annualPremium.toLocaleString()} <span style={{ fontSize: 24 }}>円</span>
          </div>
          <div style={{ fontSize: 16, color: '#1E3A8A' }}>
            （月額: {result.monthlyPremium.toLocaleString()} 円）
          </div>
        </div>

        <h3 style={{ marginTop: 48, marginBottom: 16 }}>計算内訳</h3>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <tbody>
            {result.breakdowns.map((b, i) => (
              <tr key={i} style={{ borderBottom: '1px solid var(--color-border)' }}>
                <td style={{ padding: '16px 0', color: 'var(--color-text-main)' }}>{b.itemName}</td>
                <td style={{ padding: '16px 0', textAlign: 'right', fontWeight: 500 }}>
                  {b.rate ? `× ${b.rate.toFixed(3)}` : `+ ${b.amount?.toLocaleString()} 円`}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div style={{ marginTop: 48, textAlign: 'center' }}>
          <button className="btn btn-primary" onClick={() => navigate('/')}>トップへ戻る</button>
        </div>
      </div>
    </div>
  );
};
