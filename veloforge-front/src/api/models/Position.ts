/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
 
import type { GeoPoint } from './GeoPoint'
export type Position = {
  /**
   * Reference to cyclist
   */
  cyclistId: string
  location: GeoPoint
  timestamp: string
  /**
   * Distance covered in kilometers
   */
  distance: number
  /**
   * Current speed in km/h
   */
  speed: number
  /**
   * Current power output in watts
   */
  power?: number
}
