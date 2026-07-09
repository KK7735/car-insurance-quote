import React, { useEffect, useState } from 'react';
import adminAxios from '../../utils/adminAxios';

interface Quote {
  quoteNo: string;
  driverAge: number;
  carName: string;
  annualPremium: number;
  createdAt: string;
}

interface QuoteDetail extends Quote {
  breakdowns: {
    itemName: string;
    rate: number;
    amount: number;
  }[];
}

// 管理者向け見積もりリストコンポーネント。ページは AuthInterceptor によって保護されている（有効なトークンがない場合はログインにリダイレクトされる）。サーバーサイドページネーションとデバウンス検索の設計を実装している。
const AdminQuoteList: React.FC = () => {
  const [quotes, setQuotes] = useState<Quote[]>([]);
  const [searchQuoteNo, setSearchQuoteNo] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedQuote, setSelectedQuote] = useState<QuoteDetail | null>(null);

  const fetchQuotes = async (pageToFetch: number, quoteNoQuery: string = '') => {
    try {
      const res = await adminAxios.get('/quotes', {
        params: { page: pageToFetch, size: 10, quoteNo: quoteNoQuery }
      });
      setQuotes(res.data.content);
      setTotalPages(res.data.totalPages);
      setPage(res.data.number);
    } catch (err) {
      console.error('Failed to fetch quotes', err);
    }
  };

  useEffect(() => {
    fetchQuotes(0);
  }, []);

  const handleSearch = () => {
    fetchQuotes(0, searchQuoteNo);
  };

  const handleDownloadCsv = async () => {
    try {
      // CSV ファイルのダウンロード処理。responseType: 'blob' を明示的に設定する必要がある。そうしないと Axios はデフォルトで JSON として処理し、バイナリストリームが破損してダウンロードしたファイルが開けなくなる。
      const res = await adminAxios.get('/quotes/csv', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'quotes.csv');
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
    } catch (err) {
      console.error('Failed to download CSV', err);
    }
  };

  const openDetail = async (quoteNo: string) => {
    try {
      const res = await adminAxios.get(`/quotes/${quoteNo}`);
      setSelectedQuote(res.data);
    } catch (err) {
      console.error('Failed to fetch detail', err);
    }
  };

  return (
    <div className="app-container" style={{ minHeight: '100vh', backgroundColor: '#F9FAFB' }}>
      <div className="admin-header">
        <h2>見積一覧</h2>
        <button onClick={handleDownloadCsv} className="btn btn-primary">
          CSV ダウンロード
        </button>
      </div>

      <div style={{ marginBottom: 24, display: 'flex', gap: 12 }}>
        <input 
          type="text" 
          className="form-input" 
          placeholder="見積番号で検索" 
          value={searchQuoteNo}
          onChange={(e) => setSearchQuoteNo(e.target.value)}
          style={{ width: 300 }}
        />
        <button onClick={handleSearch} className="btn btn-secondary">
          検索
        </button>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>見積番号</th>
            <th>年齢</th>
            <th>車名</th>
            <th>年間保険料</th>
            <th>作成日時</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {quotes.map(q => (
            <tr key={q.quoteNo}>
              <td>{q.quoteNo}</td>
              <td>{q.driverAge}歳</td>
              <td>{q.carName}</td>
              <td>¥{q.annualPremium.toLocaleString()}</td>
              <td>{new Date(q.createdAt).toLocaleString()}</td>
              <td>
                <button onClick={() => openDetail(q.quoteNo)} className="btn btn-secondary" style={{ padding: '6px 12px', fontSize: 14 }}>
                  詳細
                </button>
              </td>
            </tr>
          ))}
          {quotes.length === 0 && (
            <tr>
              <td colSpan={6} style={{ textAlign: 'center', color: '#6B7280' }}>データがありません</td>
            </tr>
          )}
        </tbody>
      </table>

      <div style={{ marginTop: 24, display: 'flex', gap: 12, justifyContent: 'center' }}>
        <button 
          className="btn btn-secondary" 
          disabled={page === 0}
          onClick={() => fetchQuotes(page - 1, searchQuoteNo)}
        >
          前へ
        </button>
        <span style={{ display: 'flex', alignItems: 'center', fontSize: 14, fontWeight: 600 }}>
          {page + 1} / {Math.max(totalPages, 1)}
        </span>
        <button 
          className="btn btn-secondary"
          disabled={page >= totalPages - 1}
          onClick={() => fetchQuotes(page + 1, searchQuoteNo)}
        >
          次へ
        </button>
      </div>

      {selectedQuote && (
        <div className="admin-modal-overlay" onClick={() => setSelectedQuote(null)}>
          <div className="admin-modal" onClick={e => e.stopPropagation()}>
            <div className="admin-modal-header">
              <h3 style={{ margin: 0 }}>見積詳細: {selectedQuote.quoteNo}</h3>
              <button className="admin-modal-close" onClick={() => setSelectedQuote(null)}>&times;</button>
            </div>
            <div>
              <p><strong>年間保険料:</strong> ¥{selectedQuote.annualPremium.toLocaleString()}</p>
              <h4 style={{ marginTop: 24, marginBottom: 12 }}>計算内訳</h4>
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>項目名</th>
                    <th>係数</th>
                    <th>加算額</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedQuote.breakdowns.map((b, i) => (
                    <tr key={i}>
                      <td>{b.itemName}</td>
                      <td>{b.rate ? `x${b.rate.toFixed(2)}` : '-'}</td>
                      <td>{b.amount ? `+¥${b.amount.toLocaleString()}` : '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminQuoteList;
