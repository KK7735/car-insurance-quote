import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { TopPage } from './pages/TopPage';
import { WizardLayout } from './pages/WizardLayout';
import { Step1 } from './pages/steps/Step1';
import { Step2 } from './pages/steps/Step2';
import { Step3 } from './pages/steps/Step3';
import { Step4 } from './pages/steps/Step4';
import { Confirm } from './pages/steps/Confirm';
import { ResultPage } from './pages/ResultPage';
import AdminLogin from './pages/admin/AdminLogin';
import AdminQuoteList from './pages/admin/AdminQuoteList';

// グローバルルーティングの定義。`/quote` 以下のサブルートはネストされたルーティングを通じてウィザード形式のマルチステップフォームを実現し、状態は親コンポーネントである WizardLayout に保持される。
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<TopPage />} />
        <Route path="/quote" element={<WizardLayout />}>
          <Route path="step1" element={<Step1 />} />
          <Route path="step2" element={<Step2 />} />
          <Route path="step3" element={<Step3 />} />
          <Route path="step4" element={<Step4 />} />
          <Route path="confirm" element={<Confirm />} />
        </Route>
        <Route path="/result/:quoteNo" element={<ResultPage />} />
        
        {/* Admin Routes */}
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin/quotes" element={<AdminQuoteList />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
