import client from './client';

export interface OptionCreateRequest {
  name: string;
  description?: string;
  tags?: string;
  imageUrl?: string;
}

export interface OptionUpdateRequest {
  name?: string;
  description?: string;
  tags?: string;
  imageUrl?: string;
}

export interface OptionVO {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  description?: string;
  tags: string[];
  imageUrl?: string;
  createdAt: string;
}

export const optionApi = {
  list: (categoryId: number) => client.get(`/categories/${categoryId}/options`),
  create: (categoryId: number, data: OptionCreateRequest) =>
    client.post(`/categories/${categoryId}/options`, data),
  batchCreate: (categoryId: number, data: OptionCreateRequest[]) =>
    client.post(`/categories/${categoryId}/options/batch`, data),
  get: (id: number) => client.get(`/options/${id}`),
  update: (id: number, data: OptionUpdateRequest) => client.put(`/options/${id}`, data),
  delete: (id: number) => client.delete(`/options/${id}`),
};
