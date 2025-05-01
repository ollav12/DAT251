import { request } from './transport'

export function getUserIdFromLocalStorage(): string | null {
  return localStorage.getItem('userId')
}

export type User = {
  id: string
  email: string
  name: string
  admin: boolean
}

async function getUser(id: string): Promise<User> {
  try {
    const url = `/users/${id}`
    const response = await request('GET', url)
    return response
  } catch (error) {
    console.error('Error fetching user:', error)
    throw error
  }
}

export async function getMe() {
  // TODO: use correct user id
  return getUser(getUserIdFromLocalStorage() || '')
}

export function setUserIdInLocalStorage(id: number) {
  localStorage.setItem('userId', id.toString())
}

export function logout() {
  localStorage.removeItem('userId')
}
