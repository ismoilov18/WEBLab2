const all_btn = document.querySelectorAll('.btnR');

let chooseNumber = 0;
all_btn.forEach(bt => {
    bt.addEventListener('click', (e) =>{
        chooseNumber = e.target.innerHTML;
        console.log(chooseNumber)
    })
})

$('#submit_button').on('click', () => {
    if (!validate()) {
        return
    }
    requestData({
        clicked: false,
        x: $("label[for='" + $('[name="value_X"]:checked').attr('id') + "']").html(),
        y: $('#value_Y').val().replace(',', '.'),
        r: chooseNumber
    })
})

function error(message) {
    Swal.fire({
        icon: 'error',
        title: 'Ошибка',
        text: message,
        heightAuto: false
    })
}


