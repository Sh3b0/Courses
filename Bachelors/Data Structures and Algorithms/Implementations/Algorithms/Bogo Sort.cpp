#include <bits/stdc++.h>

using namespace std;

/// Deterministic Bogo Sort, O((n+1)!)

int main()
{
    int x[] = {5, 9, 1, 0, -561, 69, 98, -20, 22, 13, 97, 14}; /// Allow up to 35 seconds.
    int n = sizeof(x) / sizeof(x[0]); /// 13! = 6227020800
    int p[n], y[n];
    for(int i=0;i<n;i++)p[i]=i;
    
    do{
        for(int i=0;i<n;i++) y[i] = x[p[i]];

        if(is_sorted(y,y+n)){
            cout<<"WTF IT WORKS\n";
            for(auto u:y)cout<<u<<" ";
            return 0;
        }

    } while(next_permutation(p,p+n));

}

/**                                                                                     //os`       
                                                                                       .syyys`      
    `-----...``                                                                        /yyysy+      
    /mmmmmmdddddddhhhyysssoo+++///::                                                   +syyoyy`     
    -mmmNNNNNNNNNNNNNNNNNmmmmmmmmmmm-                                                  osys+sy`     
    `mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm/                                                 `syyo+y/      
     mmmmMMMMMMMMMMMMMMMMMMMMMMMMMMNo                                               `+ddysoyo       
     hmmmMMMMMMMMMMMMMMMMMMMMMMMMMMNy                                             `sssyyyyyy`       
     smmmMMMMMMMMMMMMMMMMMMMMMMMMMMNd                                   `:++/-...-shyyyyyyhdh:      
     +mmmNMMMMMMMMMMMMMMMMMMMMMMMMMNm`                                 :+yhyyssssyyhhhhhhhhhdm+`    
     /mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm.                               ./++oyyyhhhyyhhhhhhhhhhhdmy-   
     -mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm:                              `++ossoyyyhhhhhhhhhhhhhhhhdNd:  
     `mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm+                               ``-:.-syyyhhhhhhhhhhhhhhhhmNh. 
      dmmmMMMMMMMMMMMMMMMMMMMMMMMMMMNs                                  `:ossyyyhhhhhhhhhhhhhhhdmm+ 
      hmmmMMMMMMMMMMMMMMMMMMMMMMMMMMNh                                 `/++o+:.../syyhdhhhyyyyhdmm/ 
      smmmMMMMMMMMMMMMMMMMMMMMMMMMMMNd                                  ````       `-shhyyhhhhhmmm+ 
      +mmmNMMMMMMMMMMMMMMMMMMMMMMMMMNm`                                            `oyyyyyhhhhdmNy. 
      :mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm-             `:-.                         `:syyyyyhhhhhmmN/  
      .mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMm/             :hyso-.-:-`                `:oyyyyyyhyyyyh+os.  
      `mmmNMMMMMMMMMMMMMMMMMMMMMMMMMMmo             :hyhsooooso-`  ``         -syyyyyyyyhyyyhh.     
       dmmmMMMMMMMMMMMMMMMMMMMMMMMMMMNy             `hhhsosssyyso+osso:`     /yyyyyyyyyyhhhhhh:     
       ymmmMMMMMMMMMMMMMMMMMMMMMMMMMMNd              .:::/++/:::/syhhyyo:-`./yyyyyyyyyyhhhhhhh+     
       ommmMMMMMMMMMMMMMMMMNNNNNNNNNmmm`                         `/osyhhyyssyyyyyyyyyyyyyhhhhho     
       +mmmNNNNNNNNNmmmmmmmmmmmmmmmmmmm.                            `.+hhyyyyyyyyhhyhyyyhhhhhhy`    
       :mmmmmmmmmmmmmmmmmmmmmmmmmmmmddd-                       ```   :oyhhhhhhhhhhhhhhhyyyhhhhh/    
       .mmmmmmmmmmmmmmmddddhyso+/::-.``                      `/+o+//oyysyyyyhhhhhhhhhyyyyyyhyyhy`   
        mmddddhhdmmmmd-..`                                  `oosssso++ooo:.`-syhhhhyyyyyyyyyyhhh+   
        --..` `-:mmymm:                            ``..---::osssss+-.``.```  .syyyyyyyyyyyyhhyhhy   
              -  ymdhmh                    ``..---::::::::/yyyyyy+::::::::::::/+++ossyyyyyyyyyyyy:` 
             ..  -mmymm/          ``..---:::::::::::::::::ydddmmd::::-::::::/++++++++++osyyyyyyyyys+
            `.    smmmmd``.--::://///++osyyhdmmmdhhhhyyssymmmdddo---------:+oooooooooooo++osyyyyyyyy
...`       ..   /+ymmmmmmmdddmmmmmdmNNNNNNNNNNNNNNmmmmmmmmmdh+///:::----:/+ooossossssssoooo++oyyysss
   `....--:---:smmmmmmddddmmmNNNNNNNNNNNNNmmmmmmmmmNmdhys+///::::---:://+oossosososssssssssooo++ooss
`..--::////////+dmmNNNNNNNNNNNNmmmddmmmmmmmNNmmdhso/::----------:://++++ooooossooossosssssssssso+++/
::://///////:::shddmmNNNNNNNNNNNmmmNNmmdhyso+/::----...---:://++ooooo+/+ooooossoossssosssssssssooooo
*/