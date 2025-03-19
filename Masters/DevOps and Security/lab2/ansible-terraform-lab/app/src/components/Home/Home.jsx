import React, { useEffect, useState } from "react";
import NavBar from "../common/NavBar/NavBar";
import Select from "../common/Select/Select";
import Search from "../common/Search/Search";
import BirdsList from "../common/BirdsList/BirdsList";
import Footer from "../common/Footer/Footer";
import Loader from "../common/Loader/Loader";
import { searchFunc, fetchData } from "../../utils/fetchData";
import * as Sentry from '@sentry/react';
import { SENTRY_BAGGAGE_KEY_PREFIX } from "@sentry/utils";

const Home = () => {
  const [country, setCountry] = useState("Russia");
  const [birds, setBirds] = useState(null);
  const [filteredBirds, setFilteredBirds] = useState(null);
  const [loading, setLoading] = useState(true);
  const imagePerRow = 20;
  const [next, setNext] = useState(imagePerRow);

  const handleMoreImage = () => {
    setNext(next + imagePerRow);
  };
  const handleNextCountry = (e) => {
    setCountry(e.target.value);
  };

  const handleSearch = (e) => {
    setFilteredBirds(birds.filter((bird) => searchFunc(bird, e.target.value)));
  };

  useEffect(() => {
    fetchData(country, setBirds, setFilteredBirds, setLoading);
  }, [country]);

  return (
    <Sentry.ErrorBoundary fallback={<p>An error has occurred in Home File</p>}>
    <>
      {!loading ? (
        <div className="relative">
          <NavBar />
          <div className="flex flex-col justify-center items-center">
            <main className="mt-24   mb-52">
              <div className="flex flex-col md:flex-row md:items-center gap-10 md:gap-16 ">
                <Select
                  country={country}
                  handleNextCountry={handleNextCountry}
                />
                <Search handleSearch={handleSearch} />
              </div>
              <div className="mt-20 space-y-16">
                <BirdsList
                  birdsList={filteredBirds}
                  next={next}
                  country={country}
                />
                {next < filteredBirds.length ? (
                  <div className="flex justify-center">
                    <button
                      className="load-btn text-[18px]"
                      onClick={handleMoreImage}
                    >
                      Load more ...
                    </button>
                  </div>
                ) : (
                  <div className="flex justify-center">
                    <span className=" color-1">
                      You have reached the end of the list
                    </span>
                  </div>
                )}
              </div>
            </main>
          </div>
          <div className="bg-gray-200 h-[1px] mx-[22px] md:mx-[82px]"></div>
          <Footer />
        </div>
      ) : (
        <Loader />
      )}
    </>
    </Sentry.ErrorBoundary>
  );
};

export default Home;
