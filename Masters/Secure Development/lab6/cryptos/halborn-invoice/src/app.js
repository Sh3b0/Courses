const express = require('express')
const payments = require('./payments')
const pdf = require('./pdf')
const app = express()
const port = 3131
const { v4: uuidv4 } = require('uuid');
const { readFile } = require('fs/promises')
const path = require('node:path');
const cookieParser = require('cookie-parser')

var invoice = null

app.use(express.urlencoded({extended: true}));
app.use(express.static('static'))
app.use(cookieParser())

app.get('/', (req, res) => {
  res.sendFile('index.html', { root: path.join(__dirname, 'templates')})
})

app.get('/create', (req, res) => {
    res.sendFile('create.html', { root: path.join(__dirname, 'templates')})
})

app.post('/invoice', async (req, res) => {
  let bonusRate = payments.validateBonus(req.body.role)
  let id = uuidv4();
  
  let total = 1337
  //TODO: add this invoice for all roles of company
  //This invoice is ONLY for Offensive Security Engineer
  if (total * (1 - bonusRate)> 0) {
    try {
      return res.redirect(payments.getPaymentURL(id))
    } catch (e) {
      res.statusCode = 500
      return res.send(e.message)
    }
  }
  
  //TODO: add this bonus value to invoice
  let pdffile = await pdf.renderPdf(req.body)
  res.setHeader('Content-Type', 'application/pdf');
  res.setHeader('Content-Disposition', 'inline; filename=invoice.pdf')
  return res.send(pdffile)
})  

app.get('/renderInvoice', async (req, res) => {
  if (!invoice) {
    invoice = await readFile('templates/invoice.html', 'utf8')
  }

  let html = invoice
  .replaceAll("{{ name }}", req.query.name)
  .replaceAll("{{ address }}", req.query.address)
  .replaceAll("{{ phone }}", req.query.phone)
  .replaceAll("{{ email }}", req.query.email)
  .replaceAll("{{ role }}", req.query.role)
  res.setHeader("Content-Type", "text/html")
  res.setHeader("Content-Security-Policy", "default-src 'unsafe-inline' maxcdn.bootstrapcdn.com; object-src 'none'; script-src 'none'; img-src 'self' dummyimage.com;")
  res.send(html)
})

app.get('/secret', (req, res) => {
  if (req.socket.remoteAddress != "::ffff:127.0.0.1") {
    return res.send("Ok try harder")
  }
  if (req.cookies['bot']) {
    return res.send("Ok try harder")
  }
  res.setHeader('X-Frame-Options', 'none');
  res.send(process.env.FLAG || 'secret')
})

app.listen(port, () => {
  console.log(`Halborn Invoice app listening on port ${port}`)
})
