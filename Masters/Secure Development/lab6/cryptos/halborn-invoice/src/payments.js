function getPaymentURL(cartId){
    throw new Error('We regret to inform you that only an invoice and bonus are applicable to the Offensive Security Engineer role.')
}

let roles =  {
    "Offensive Security Engineer": 100
}

function validateBonus(role) {
    return (roles[role] ?? 0) / 100
}

module.exports = { 
    getPaymentURL,
    validateBonus,
};
