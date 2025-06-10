import Input from "../common/Input";
import Button from "../common/Button";
import "../pages/BoardWritePage.css";
import UploadBoard from "../../assets/upload.svg";
import { useState } from "react";

const BoardWrite = () => {
    const [imgFile, setImgFile] = useState('');
    return (
        <div className ="input-tag-outline">
            <div className="input-tag">
                <Input type="file" 
                    accept='image/jpg, image/png, image/jpeg, image/gif'
                    name='board_upload_img'/>
                <Input placeholder={"게시글 입력란"}/>
                <Button style={{width: "50px"}}>
                    <img src={UploadBoard} alt="업로드 아이콘"/>
                </Button>
            </div>
        </div>
    );
}
export default BoardWrite;