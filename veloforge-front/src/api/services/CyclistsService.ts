/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */

import type { CreateCyclistRequest } from '../models/CreateCyclistRequest'
import type { Cyclist } from '../models/Cyclist'

import type { CancelablePromise } from '../core/CancelablePromise'
import { OpenAPI } from '../core/OpenAPI'
import { request as __request } from '../core/request'

export class CyclistsService {
  /**
   * List all cyclists
   * @returns Cyclist List of cyclists
   * @throws ApiError
   */
  public static listCyclists(): CancelablePromise<Array<Cyclist>> {
    return __request(OpenAPI, {
      method: 'GET',
      url: '/cyclists',
    })
  }

  /**
   * Create a new cyclist
   * @param requestBody
   * @returns Cyclist Cyclist created
   * @throws ApiError
   */
  public static createCyclist(requestBody: CreateCyclistRequest): CancelablePromise<Cyclist> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/cyclists',
      body: requestBody,
      mediaType: 'application/json',
    })
  }

  /**
   * Get cyclist by ID
   * @param cyclistId
   * @returns Cyclist Cyclist details
   * @throws ApiError
   */
  public static getCyclist(cyclistId: string): CancelablePromise<Cyclist> {
    return __request(OpenAPI, {
      method: 'GET',
      url: '/cyclists/{cyclistId}',
      path: {
        cyclistId: cyclistId,
      },
      errors: {
        404: `Cyclist not found`,
      },
    })
  }
}
