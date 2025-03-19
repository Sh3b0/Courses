import React, { useState, useEffect } from "react";
import birdImg from "../../../assets/placeholder.png";
import ReactAudioPlayer from "react-audio-player";
import fetchImage from "../../../utils/FetchImage";
import * as Sentry from '@sentry/react';

const BirdCard = ({ bird }) => {
  const [imageUrl, setImageUrl] = useState("");

  useEffect(() => {
    fetchImage(bird,setImageUrl);
  }, [bird]);

  let regex = /([A-Z])\w+/g;
  const sono = bird.sono.small.match(regex)[0];
  const fileName = decodeURIComponent(bird["file-name"]);
  const audioUrl = `https://xeno-canto.org/sounds/uploaded/${sono}/${fileName}`;

  return (
   <Sentry.ErrorBoundary fallback={<p>An error has occurred in BirdCard File</p>}>
    <div className="relative rounded-xl border border-gray-200 h-[540px] max-w-[283px]">
      <img
        className="w-full h-[239px] bird-img"
        src={imageUrl ? imageUrl : birdImg}
        alt="bird"
      />

      <div className="flex flex-col gap-6 p-6">
        <h3 className="color-2 font-bold text-[24px] specie-identity">
          {bird.en ? bird.en : "Identity Unknown"}
        </h3>
        <div className="flex flex-col gap-4 text-[16px]">
          <div className="flex gap-5 item-center specie-info">
            <span className="color-4">🐤 Specie</span>
            <span className="color-3">{bird.sp}</span>
          </div>
          <div className="flex gap-5 item-center location-info">
            <span className="color-4 whitespace-nowrap">📍 Place</span>
            <span className="color-3">{bird.loc}</span>
          </div>
        </div>
      </div>
      <div className="absolute left-0 right-0 bottom-0">
        <div className="flex flex-col gap-6 p-6">
          <div className="bg-gray-200 h-[1px]"></div>
          <ReactAudioPlayer
            className="h-[40px] w-full"
            src={audioUrl}
            controls
          />
        </div>
      </div>
    </div>
   </Sentry.ErrorBoundary>
  );
};

export default Sentry.withProfiler(BirdCard);
