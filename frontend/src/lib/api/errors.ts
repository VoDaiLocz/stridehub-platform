import axios from 'axios'
import type { ApiErrorResponse } from './types'

export function getApiErrorMessage(error: unknown, fallbackMessage: string): string {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.message ?? fallbackMessage
  }

  if (error instanceof Error && error.message) {
    return error.message
  }

  return fallbackMessage
}
