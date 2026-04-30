import client from './client';
import { OptionVO } from './optionApi';

export interface PreferenceVO {
  tag: string;
  weight: number;
}

export interface DailyRecommendationVO {
  timePeriod: string;
  message: string;
  recommendationsByCategory: Record<string, OptionVO[]>;
}

export const recommendationApi = {
  getPreferences: (categoryId: number) => client.get(`/categories/${categoryId}/preferences`),
  updatePreferences: (categoryId: number, preferences: PreferenceVO[]) =>
    client.put(`/categories/${categoryId}/preferences`, preferences),
  getDaily: () => client.get('/recommendations/daily'),
  getWeekly: () => client.get('/recommendations/weekly'),
};
