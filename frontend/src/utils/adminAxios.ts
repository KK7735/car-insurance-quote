import axios from 'axios';

const adminAxios = axios.create({
  baseURL: 'http://localhost:8080/api/admin',
});

// リクエストインターセプター：localStorage から管理者トークンを自動的に読み取り、Authorization ヘッダーに注入する。
adminAxios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

adminAxios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // レスポンスインターセプター：401 Unauthorized エラーをグローバルにキャッチする。トークンが無効になった場合、ローカルキャッシュを自動的にクリアし、ログインページにリダイレクトする。
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('adminToken');
      window.location.href = '/admin/login';
    }
    return Promise.reject(error);
  }
);

export default adminAxios;
