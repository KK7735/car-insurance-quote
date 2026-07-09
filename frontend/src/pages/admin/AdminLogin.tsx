import React, { useState } from 'react';
import { useForm, FormProvider } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { FormInput } from '../../components/FormInput';
import adminAxios from '../../utils/adminAxios';

const loginSchema = z.object({
  username: z.string().min(1, 'ユーザー名を入力してください'),
  password: z.string().min(1, 'パスワードを入力してください'),
});

type LoginFormData = z.infer<typeof loginSchema>;

const AdminLogin: React.FC = () => {
  const navigate = useNavigate();
  const [serverError, setServerError] = useState('');
  
  const methods = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      setServerError('');
      const response = await adminAxios.post('/login', data);
      localStorage.setItem('adminToken', response.data.token);
      navigate('/admin/quotes');
    } catch (err: any) {
      setServerError('ユーザー名またはパスワードが間違っています');
    }
  };

  return (
    <div className="app-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', backgroundColor: '#F9FAFB' }}>
      <div className="card" style={{ width: '100%', maxWidth: 400 }}>
        <h2 style={{ textAlign: 'center', marginBottom: 32 }}>管理者ログイン</h2>
        
        {serverError && (
          <div style={{ backgroundColor: '#FEE2E2', color: '#DC2626', padding: 12, borderRadius: 8, marginBottom: 24, fontSize: 14 }} data-testid="server-error">
            {serverError}
          </div>
        )}

        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)}>
            <FormInput name="username" label="ユーザー名" type="text" placeholder="admin" />
            <FormInput name="password" label="パスワード" type="password" placeholder="••••••••" />
            
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: 24 }}>
              ログイン
            </button>
          </form>
        </FormProvider>
      </div>
    </div>
  );
};

export default AdminLogin;
