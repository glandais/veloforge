/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */

import type { CyclistCapabilities } from './CyclistCapabilities'
import type { CyclistState } from './CyclistState'
export type Cyclist = {
  /**
   * Unique cyclist identifier
   */
  id: string
  /**
   * Cyclist name
   */
  name: string
  capabilities: CyclistCapabilities
  state?: CyclistState
  createdAt?: string
}
