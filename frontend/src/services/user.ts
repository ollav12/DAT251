import { request } from './transport'

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
  return getUser('1')
}
