import * as Sentry from "@sentry/browser";

function uniq(a, key) {
  var seen = {};
  return a.filter(function(item) {
    const k = key(item)
    return seen.hasOwnProperty(k) ? false : (seen[k] = true);
  });
}

export const fetchData = async (
  country,
  setBirds,
  setFilteredBirds,
  setLoading
) => {
  try {
    setLoading(true);
    const res = await fetch(
      `https://xeno-canto.org/api/2/recordings?query=cnt:${country}&page=1`,
      {
        method: "GET",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          "Accept-Language": "en-US",
        },
      }
    );
    const results = await res.json();
    const records = uniq(results.recordings, r => r.en)
    setBirds(records);
    setFilteredBirds(records);
    setLoading(false);
    return "";
  } catch (error) {
    console.log(error.message);
    Sentry.captureException(error);

  }
};

export const searchFunc = (bird, item) =>
  bird.en.toLowerCase().includes(item.toLowerCase().trim()) ||
  bird.sp.toLowerCase().includes(item.toLowerCase().trim()) ||
  bird.loc.toLowerCase().includes(item.toLowerCase().trim());
