/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */

import type { GeoPoint } from './GeoPoint'
export type Waypoint = {
  position: GeoPoint
  /**
   * Distance from start in kilometers
   */
  distance: number
  /**
   * Elevation in meters
   */
  elevation?: number
}
