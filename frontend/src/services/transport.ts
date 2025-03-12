const baseURL = 'http://localhost:8080'

export type Statistics = {
  totalTrips: number
  totalDistanceKm: number
  totalDurationSeconds: number

  totalEmissionsCO2eKg: number
  totalEmissionsSavingsCO2eKg: number

  totalCostNOK: number
  totalSavingsNOK: number
}

async function getStatistics(): Promise<Statistics> {
  try {
    const url = '/transport/statistics?userId=1'
    const response = await request('GET', url)
    return response
  } catch (error) {
    console.error('Error fetching statistics:', error)
    throw error
  }
}

export type Trip = {
  id: string
  origin: string
  destination: string
  distance: number
  vehicle?: Vehicle
  travelMode: string
  totalDistanceKm: number
  totalDurationSeconds: number
  totalEmissionsCO2eKg: number
  savedEmissionsCO2eKg: number
}

type CreateTrip = {
  origin: string
  destination: string
  mode: string
  vehicleId: string
}

async function createTrip(trip: CreateTrip) {
  try {
    const url = '/trips?userId=1'
    const response = await request('POST', url, trip)
    return response
  } catch (error) {
    console.error('Error creating trip:', error)
    throw error
  }
}

async function listTrips(): Promise<Trip[]> {
  try {
    const url = '/trips?userId=1'
    const response = await request('GET', url)
    return response
  } catch (error) {
    console.error('Error fetching trips:', error)
    throw error
  }
}

export type Vehicle = {
  id: number
  make: string
  model: string
  type: string
  year: number
  emissionsCO2ePerKm: number
  default: boolean
}

async function listVehicles(): Promise<Vehicle[]> {
  try {
    const url = '/transport/vehicles?userId=1'
    const response = await request('GET', url)
    return response
  } catch (error) {
    console.error('Error fetching vehicles:', error)
    throw error
  }
}

type CreateVehicle = Pick<Vehicle, 'make' | 'model' | 'type' | 'year' | 'emissionsCO2ePerKm'>

async function createVehicle(vehicle: CreateVehicle) {
  try {
    const url = '/transport/vehicles'
    const response = await request('POST', url, vehicle)
    return response
  } catch (error) {
    console.error('Error creating vehicle:', error)
    throw error
  }
}

async function setDefaultVehicle(id: Vehicle['id']) {
  try {
    const url = `/transport/vehicles/${id}/default`
    const response = await request('PUT', url)
    return response
  } catch (error) {
    console.error('Error setting default vehicle:', error)
    throw error
  }
}

async function deleteVehicle(id: Vehicle['id']) {
  try {
    const url = `/transport/vehicles/${id}`
    const response = await request('DELETE', url)
    return response
  } catch (error) {
    console.error('Error deleting vehicle:', error)
    throw error
  }
}

export default {
  getStatistics,

  createTrip,
  listTrips,

  createVehicle,
  listVehicles,
  setDefaultVehicle,
  deleteVehicle,
}

async function request(method: string, url: string, body?: unknown) {
  try {
    const response = await fetch(`${baseURL}${url}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        // TODO: 'Authorization': 'Bearer token',
      },
      body: body ? JSON.stringify(body) : undefined,
    })
    if (!response.ok) {
      throw new Error(`HTTP error: status: ${response.status}`)
    }
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error making request:', error)
    throw error
  }
}
