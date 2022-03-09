vector<string> split(string s, char delim)
{
    stringstream i(s);
    vector<string>r;
    string token;

    while(getline(i, token, delim))
        r.push_back(token);

    return r;
}