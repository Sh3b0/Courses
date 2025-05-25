
const express     = require('express');
const session     = require('express-session');
const bodyParser  = require('body-parser');
const crypto      = require('crypto');
const MongoClient = require('mongodb').MongoClient;
const ObjectID    = require('mongodb').ObjectID;
const path        = require("path");
const spawn        = require('child_process').spawn;
const app         = express();
const url         = 'mongodb://mark:5AYRft73VtFpc84k@localhost:27017/myplace?authMechanism=DEFAULT&authSource=myplace';
const backup_key  = '45fac180e9eee72f4fd2d9386ea7033e52b7c740afc3d98a8d0230167104d474';

MongoClient.connect(url, function(error, db) {
  if (error || !db) {
    console.log('[!] Failed to connect to mongodb');
    return;
  }

  app.use(session({
    secret: 'the boundless tendency initiates the law.',
    cookie: { maxAge: 3600000 },
    resave: false,
    saveUninitialized: false
  }));

  app.use(function (req, res, next) {
    var agent = req.headers['user-agent'];
    var blacklist = /(DirBuster)|(Postman)|(Mozilla\/4\.0.+Windows NT 5\.1)|(Go\-http\-client)/i;

    if (!blacklist.test(agent)) {
      next();
    }
    else {
      count = Math.floor((Math.random() * 10000) + 1);
      randomString = '';

      var charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      for (var i = 0; i < count; i++)
        randomString += charset.charAt(Math.floor(Math.random() * charset.length));

      res.set('Content-Type', 'text/plain').status(200).send(
        [
          'QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ',
          'QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ',
          'QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ',
          'QQQQQQQQQQQQQQQQQQQWQQQQQWWWBBBHHHHHHHHHBWWWQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ',
          'QQQQQQQQQQQQQQQD!`__ssaaaaaaaaaass_ass_s____.  -~""??9VWQQQQQQQQQQQQQQQQQQQ',
          'QQQQQQQQQQQQQP\'_wmQQQWWBWV?GwwwmmWQmwwwwwgmZUVVHAqwaaaac,"?9$QQQQQQQQQQQQQQ',
          'QQQQQQQQQQQW! aQWQQQQW?qw#TTSgwawwggywawwpY?T?TYTYTXmwwgZ$ma/-?4QQQQQQQQQQQ',
          'QQQQQQQQQQW\' jQQQQWTqwDYauT9mmwwawww?WWWWQQQQQ@TT?TVTT9HQQQQQQw,-4QQQQQQQQQ',
          'QQQQQQQQQQ[ jQQQQQyWVw2$wWWQQQWWQWWWW7WQQQQQQQQPWWQQQWQQw7WQQQWWc)WWQQQQQQQ',
          'QQQQQQQQQf jQQQQQWWmWmmQWU???????9WWQmWQQQQQQQWjWQQQQQQQWQmQQQQWL 4QQQQQQQQ',
          'QQQQQQQP\'.yQQQQQQQQQQQP"       <wa,.!4WQQQQQQQWdWP??!"??4WWQQQWQQc ?QWQQQQQ',
          'QQQQQP\'_a.<aamQQQW!<yF "!` ..  "??$Qa "WQQQWTVP\'    "??\' =QQmWWV?46/ ?QQQQQ',
          'QQQP\'sdyWQP?!`.-"?46mQQQQQQT!mQQgaa. <wWQQWQaa _aawmWWQQQQQQQQQWP4a7g -WWQQ',
          'QQ[ j@mQP\'adQQP4ga, -????" <jQQQQQWQQQQQQQQQWW;)WQWWWW9QQP?"`  -?QzQ7L ]QQQ',
          'QW jQkQ@ jWQQD\'-?$QQQQQQQQQQQQQQQQQWWQWQQQWQQQc "4QQQQa   .QP4QQQQfWkl jQQQ',
          'QE ]QkQk $D?`  waa "?9WWQQQP??T?47`_aamQQQQQQWWQw,-?QWWQQQQQ`"QQQD\Qf(.QWQQ',
          'QQ,-Qm4Q/-QmQ6 "WWQma/  "??QQQQQQL 4W"- -?$QQQQWP`s,awT$QQQ@  "QW@?$:.yQQQQ',
          'QQm/-4wTQgQWQQ,  ?4WWk 4waac -???$waQQQQQQQQF??\'<mWWWWWQW?^  ` ]6QQ\' yQQQQQ',
          'QQQQw,-?QmWQQQQw  a,    ?QWWQQQw _.  "????9VWaamQWV???"  a j/  ]QQf jQQQQQQ',
          'QQQQQQw,"4QQQQQQm,-$Qa     ???4F jQQQQQwc <aaas _aaaaa 4QW ]E  )WQ`=QQQQQQQ',
          'QQQQQQWQ/ $QQQQQQQa ?H ]Wwa,     ???9WWWh dQWWW,=QWWU?  ?!     )WQ ]QQQQQQQ',
          'QQQQQQQQQc-QWQQQQQW6,  QWQWQQQk <c                             jWQ ]QQQQQQQ',
          'QQQQQQQQQQ,"$WQQWQQQQg,."?QQQQ\'.mQQQmaa,.,                . .; QWQ.]QQQQQQQ',
          'QQQQQQQQQWQa ?$WQQWQQQQQa,."?( mQQQQQQW[:QQQQm[ ammF jy! j( } jQQQ(:QQQQQQQ',
          'QQQQQQQQQQWWma "9gw?9gdB?QQwa, -??T$WQQ;:QQQWQ ]WWD _Qf +?! _jQQQWf QQQQQQQ',
          'QQQQQQQQQQQQQQQws "Tqau?9maZ?WQmaas,,    --~-- ---  . _ssawmQQQQQQk 3QQQQWQ',
          'QQQQQQQQQQQQQQQQWQga,-?9mwad?1wdT9WQQQQQWVVTTYY?YTVWQQQQWWD5mQQPQQQ ]QQQQQQ',
          'QQQQQQQWQQQQQQQQQQQWQQwa,-??$QwadV}<wBHHVHWWBHHUWWBVTTTV5awBQQD6QQQ ]QQQQQQ',
          'QQQQQQQQQQQQQQQQQQQQQQWWQQga,-"9$WQQmmwwmBUUHTTVWBWQQQQWVT?96aQWQQQ ]QQQQQQ',
          'QQQQQQQQQQWQQQQWQQQQQQQQQQQWQQma,-?9$QQWWQQQQQQQWmQmmmmmQWQQQQWQQW(.yQQQQQW',
          'QQQQQQQQQQQQQWQQQQQQWQQQQQQQQQQQQQga%,.  -??9$QQQQQQQQQQQWQQWQQV? sWQQQQQQQ',
          'QQQQQQQQQWQQQQQQQQQQQQQQWQQQQQQQQQQQWQQQQmywaa,;~^"!???????!^`_saQWWQQQQQQQ',
          'QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQWWWWQQQQQmwywwwwwwmQQWQQQQQQQQQQQ',
          'QQQQQQQWQQQWQQQQQQWQQQWQQQQQWQQQQQQQQQQQQQQQQWQQQQQWQQQWWWQQQQQQQQQQQQQQQWQ',
          '',
          '',
          '<!-- ' + randomString + ' -->'
        ].join("\n")
      );
    }
  });

  app.use(express.static(path.join(__dirname, 'static')));
  app.use(bodyParser.json());
  app.use(function(err, req, res, next) {
    if (err) {
      res.status(err.status || 500);
      res.send({
        message:"Uh oh, something went wrong!",
        error: true
      });
    }
    else {
      next();
    }
  });

  app.get('/api/users/?', function (req, res) {
    db.collection('users').find().toArray(function (error, docs) {
      if (error) {
        res.status(500).send({ error: true });
      }
      else if (!docs) {
        res.status(404).send({ not_found: true });
      }
      else {
        res.send(docs);
      }
    });
  });

  app.get('/api/users/latest', function (req, res) {
    db.collection('users').find({ is_admin: false }).toArray(function (error, docs) {
      if (error) {
        res.status(500).send({ error: true });
      }
      else if (!docs) {
        res.status(404).send({ not_found: true });
      }
      else {
        res.send(docs);
      }
    });
  });

  app.get('/api/users/:username', function (req, res) {
    db.collection('users').findOne({ username: req.params.username }, function (error, doc) {
      if (error) {
        res.status(500).send({ error: true });
      }
      else if (!doc) {
        res.status(404).send({ not_found: true });
      }
      else {
        res.send(doc);
      }
    });
  });

  app.get('/api/session', function (req, res) {
    if (req.session.user) {
      res.send({
        authenticated: true,
        user: req.session.user
      });
    }
    else {
      res.send({
        authenticated: false
      });
    }
  });

  app.post('/api/session/authenticate', function (req, res) {
    var failureResult = {
      error: true,
      message: 'Authentication failed'
    };

    if (!req.body.username || !req.body.password) {
      res.send(failureResult);
      return;
    }

    db.collection('users').findOne({ username: req.body.username }, function (error, doc) {
      if (error) {
        res.status(500).send({
          message:"Uh oh, something went wrong!",
          error: true
        });

        return;
      }

      if (!doc) {
        res.send(failureResult);
        return;
      }

      var hash = crypto.createHash('sha256');
      var cipherText = hash.update(req.body.password).digest('hex');

      if (cipherText == doc.password) {
        req.session.user = doc;
        res.send({
          success: true
        });
      }
      else {
        res.send({
          success: false
        })
      }
    });
  });

  app.get('/api/admin/backup', function (req, res) {
    if (req.session.user && req.session.user.is_admin) {
      var proc = spawn('/usr/local/bin/backup', ['-q', backup_key, __dirname ]);
      var backup = '';

      proc.on("exit", function(exitCode) {
        res.header("Content-Type", "text/plain");
        res.header("Content-Disposition", "attachment; filename=myplace.backup");
        res.send(backup);
      });

      proc.stdout.on("data", function(chunk) {
        backup += chunk;
      });

      proc.stdout.on("end", function() {
      });
    }
    else {
      res.send({
        authenticated: false
      });
    }
  });

  app.use(function(req, res, next){
    res.sendFile('app.html', { root: __dirname });
  });

  app.listen(3000, function () {
    console.log('MyPlace app listening on port 3000!')
  });

});
