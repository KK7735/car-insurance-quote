import axios from 'axios';
import { QuoteRequest, QuoteResponse } from './types';

// 一般ユーザー向けの公開 Axios インスタンス。ベース URL と統一されたリクエストヘッダーが設定されており、インターセプターや認証ロジックは含まれていない。
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

export const quoteApi = {
    createQuote: async (request: QuoteRequest): Promise<QuoteResponse> => {
        const response = await apiClient.post<QuoteResponse>('/quotes', request);
        return response.data;
    },
    getQuote: async (quoteNo: string): Promise<QuoteResponse> => {
        const response = await apiClient.get<QuoteResponse>(`/quotes/${quoteNo}`);
        return response.data;
    }
};
