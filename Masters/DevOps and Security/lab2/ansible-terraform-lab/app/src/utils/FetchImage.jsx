import * as Sentry from "@sentry/browser";

const fetchImage = async (bird, setImageUrl) => {
    try {
        const searchText = bird.en.toLowerCase()
        const options = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Accept-Language": "en-US",
            }
        }

        const pageReq = await fetch(
            `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${searchText}&srprop=snippet&format=json&origin=*`,
            options
        );
        const pageResp = await pageReq.json();
        const pageName = pageResp.query.search[0].title;
        const imageReq = await fetch(
            `https://en.wikipedia.org/w/api.php?action=query&titles=${pageName}&prop=pageimages&piprop=original&format=json&origin=*`,
            options
        );
        const imageResp = await imageReq.json();
        const imageLink = Object.values(imageResp.query.pages)[0].original.source;

        setImageUrl(imageLink);
    } catch (error) {
        Sentry.captureMessage(error.message);

        console.log(error.message);
    }
};

export default fetchImage;
