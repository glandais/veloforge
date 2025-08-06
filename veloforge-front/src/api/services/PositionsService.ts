/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */

import type { Position } from '../models/Position'

import type { CancelablePromise } from '../core/CancelablePromise'
import { OpenAPI } from '../core/OpenAPI'
import { request as __request } from '../core/request'

export class PositionsService {
  /**
   * Get current positions for all participants
   * @param eventId
   * @returns Position Current positions
   * @throws ApiError
   */
  public static getCurrentPositions(eventId: string): CancelablePromise<Array<Position>> {
    return __request(OpenAPI, {
      method: 'GET',
      url: '/events/{eventId}/positions',
      path: {
        eventId: eventId,
      },
    })
  }
}
