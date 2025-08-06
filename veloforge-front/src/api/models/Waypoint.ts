/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { GeoPoint } from './GeoPoint';

export type Waypoint = {
    position: GeoPoint;
    /**
     * Distance from start in kilometers
     */
    distance: number;
    /**
     * Elevation in meters
     */
    elevation?: number;
};

