/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { Route } from './Route'

export type CreateEventRequest = {
  name: string
  type: CreateEventRequest.type
  route: Route
}

export namespace CreateEventRequest {
  export enum type {
    POINT_TO_POINT = 'point-to-point',
    LOOP = 'loop',
    BREVET = 'brevet',
  }
}
