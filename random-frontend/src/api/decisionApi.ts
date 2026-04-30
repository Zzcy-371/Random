import client from './client';
import { OptionVO } from './optionApi';

export interface DecideRequest {
  preferenceFilters?: string[];
  excludeRecent?: boolean;
  excludeRecentDays?: number;
}

export interface DecisionVO {
  decisionId: number;
  chosenOption: OptionVO;
  alternates: OptionVO[];
  decidedAt: string;
}

export const decisionApi = {
  decide: (categoryId: number, data?: DecideRequest) =>
    client.post(`/categories/${categoryId}/decide`, data || {}),
  getHistory: (page = 0, size = 20) =>
    client.get(`/decisions?page=${page}&size=${size}`),
  getDecision: (id: number) => client.get(`/decisions/${id}`),
};
