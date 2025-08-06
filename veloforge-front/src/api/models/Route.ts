/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { GeoPoint } from './GeoPoint';
import type { Waypoint } from './Waypoint';

export type Route = {
    /**
     * Route name
     */
    name: string;
    /**
     * Total distance in kilometers
     */
    distance: number;
    startPoint: GeoPoint;
    endPoint: GeoPoint;
    waypoints?: Array<Waypoint>;
};

