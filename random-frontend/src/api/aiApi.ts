import client from './client';

export interface AiRecommendationVO {
  category: string;
  recommendation: string;
  explanation?: string;
  suggestedOptions?: string[];
  analysis?: string;
}

export const aiApi = {
  getRecommendation: (categoryId: number) => client.get(`/ai/recommend/${categoryId}`),
  suggestOptions: (categoryId: number) => client.get(`/ai/suggest/${categoryId}`),
  analyzePreferences: (categoryId: number) => client.get(`/ai/analyze/${categoryId}`),
};
