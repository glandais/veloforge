/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

export type LeaderboardEntry = {
  cyclistId: string
  cyclistName: string
  /**
   * Current ranking position
   */
  position: number
  /**
   * Distance covered in kilometers
   */
  distance: number
  status: LeaderboardEntry.status
  /**
   * Time since start (HH:MM:SS)
   */
  timeElapsed?: string
  /**
   * Average speed in km/h
   */
  averageSpeed?: number
}

export namespace LeaderboardEntry {
  export enum status {
    RIDING = 'riding',
    RESTING = 'resting',
    FINISHED = 'finished',
    DNF = 'dnf',
  }
}
