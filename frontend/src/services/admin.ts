import { request } from './transport'

export type Statistics = {
  totalUsers: number
  totalTrips: number
  totalEmissionsCO2eKg: number
  totalTripDistance: number
}

async function getStatistics(): Promise<Statistics> {
  try {
    const data = await request('GET', '/admin/statistics')
    return data
  } catch (error) {
    console.error('Error fetching statistics:', error)
    throw error
  }
}

export default {
  getStatistics,
}
