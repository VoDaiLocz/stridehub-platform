import { apiClient } from './client'
import type { OrderDetail, OrderSummary } from './types'

export async function getOrders(): Promise<OrderSummary[]> {
  const response = await apiClient.get<OrderSummary[]>('/api/v1/orders')
  return response.data
}

export async function getOrderById(orderId: string): Promise<OrderDetail> {
  const response = await apiClient.get<OrderDetail>(`/api/v1/orders/${orderId}`)
  return response.data
}
