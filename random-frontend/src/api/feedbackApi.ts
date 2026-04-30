import client from './client';

export interface FeedbackCreateRequest {
  rating: number;
  comment?: string;
}

export interface FeedbackVO {
  id: number;
  decisionId: number;
  rating: number;
  comment?: string;
  createdAt: string;
}

export const feedbackApi = {
  submit: (decisionId: number, data: FeedbackCreateRequest) =>
    client.post(`/decisions/${decisionId}/feedback`, data),
};
